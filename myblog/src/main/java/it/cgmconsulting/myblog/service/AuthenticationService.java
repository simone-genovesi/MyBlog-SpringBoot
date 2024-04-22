package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Authority;
import it.cgmconsulting.myblog.entity.Registration;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.entity.enumeration.AuthorityName;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.mail.Mail;
import it.cgmconsulting.myblog.mail.MailService;
import it.cgmconsulting.myblog.payload.request.SigninRequest;
import it.cgmconsulting.myblog.payload.request.SignupRequest;
import it.cgmconsulting.myblog.payload.response.AuthenticationResponse;
import it.cgmconsulting.myblog.repository.AuthorityRepository;
import it.cgmconsulting.myblog.repository.UserRepository;
import it.cgmconsulting.myblog.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    @Value("${application.confirmCode.validity}")
    private long validity;

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final RegistrationService registrationService;

    @Transactional
    public String signup(SignupRequest request) {
        if(userRepository.existsByUsernameOrEmail(request.getUsername(), request.getEmail()))
            return null;
        User user = new User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );
        Authority authority = authorityRepository.findByAuthorityDefaultTrue();
        user.setAuthorities(Collections.singleton(authority));
        userRepository.save(user);

        Registration registration = Registration.builder()
                .confirmCode(UUID.randomUUID().toString())
                .endDate((LocalDateTime.now().plusMinutes(validity)))
                .user(user)
                .build();
        registrationService.save(registration);

        Mail mail = mailService.createMail(user,
                "Myblog - Registration confirm",
                "Hi " + user.getUsername() +
                        ",\n please click here to confirm your email " +
                        "\n http://localhost:8090/v0/auth?confirmCode=" +
                        registration.getConfirmCode());
        mailService.sendMail(mail);
        return "User successfully registered. Please check your email to confirm the registration.";
    }

    public AuthenticationResponse signin(SigninRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            throw new BadCredentialsException("Bad credentials");

        boolean isGuest = isGuest(authorities(user.getAuthorities()));

        if(!user.isEnabled() && isGuest)
            throw new DisabledException("You didn't confirm your email still");

        if(!user.isEnabled() && !isGuest)
            throw new DisabledException("You are banned");

        String jwt = jwtService.generateToken(user, user.getId());

        return AuthenticationResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .authorities(authorities(user.getAuthorities()))
                .token(jwt)
                .build();
    }

    private String[] authorities(Collection<? extends GrantedAuthority> auths) {
        return auths.stream().map(GrantedAuthority::getAuthority)
                .toArray(String[]::new);
    }

    private boolean isGuest(String[] authorities) {
        return Arrays.stream(authorities)
                .anyMatch(s -> s
                        .contains(authorityRepository.findByAuthorityDefaultTrue()
                                .getAuthorityName()
                                .name()));
    }

    @Transactional
    public String confirm(String confirmCode){
        // verificare che il token (confirmCode) non sia scaduto
        // se è valido. abilitare lo user, cambiare l'authority
        // altrimenti reinviare l'email con nuovo token

        Optional<Registration> reg = registrationService.findByConfirmCode(confirmCode);

        if(reg.isPresent() && LocalDateTime.now().isAfter(reg.get().getEndDate())){
            Registration oldRegistration = reg.get();
            Registration newRegistration = Registration.builder()
                    .confirmCode(UUID.randomUUID().toString())
                    .endDate(LocalDateTime.now().plusMinutes(validity))
                    .user(oldRegistration.getUser())
                    .build();
            registrationService.save(newRegistration);
            registrationService.delete(oldRegistration);
            Mail mail = mailService.createMail(newRegistration.getUser(),
                    "MyBlog - Registration confirm",
                    "Hi " + newRegistration.getUser().getUsername() +
                            ",\n" +
                            "please click here to confirm your email \n" +
                            "http://localhost:8090/v0/auth/?confirmCode="+newRegistration.getConfirmCode());
            mailService.sendMail(mail);
            return "A new confirmation code has been sent to you. Please check your email";
        }
        else if (reg.isEmpty()){
            return "You already confirmed your registration";
        }    else {
            reg.get().getUser().setEnabled(true);
            Authority authority = authorityRepository.findByAuthorityName(AuthorityName.MEMBER);
            reg.get().getUser().setAuthorities(Collections.singleton(authority));
            registrationService.delete(reg.get());
            return "Now you are enable. Please log in";
        }
    }

    @Transactional
    public AuthenticationResponse changeRole(int id, String[] auths_array) {
        Set<String> authSet = new HashSet<>(Arrays.asList(auths_array));
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        Set<AuthorityName> authorityNames = new HashSet<>();
        for(String s : authSet){
            authorityNames.add(AuthorityName.valueOf(s));
        }

        Set<Authority> auths = authorityRepository.findByAuthorityNameIn(authorityNames);
        user.setAuthorities(auths);

        return AuthenticationResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .authorities(authorities(user.getAuthorities()))
                .build();
    }
}