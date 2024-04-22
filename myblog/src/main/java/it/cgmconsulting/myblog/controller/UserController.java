package it.cgmconsulting.myblog.controller;

import it.cgmconsulting.myblog.entity.enumeration.AuthorityName;
import it.cgmconsulting.myblog.payload.request.SigninRequest;
import it.cgmconsulting.myblog.payload.request.SignupRequest;
import it.cgmconsulting.myblog.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {

    private final AuthenticationService authenticationService;

    @PostMapping("/v0/auth/signup")
    public ResponseEntity<String> signup(@RequestBody @Valid SignupRequest request) {
        String msg = authenticationService.signup(request);
        if (msg == null)
            return new ResponseEntity<>("Username or email already in use", HttpStatus.CONFLICT);
        return new ResponseEntity<>(msg, HttpStatus.CREATED);
    }

    @PostMapping("/v0/auth/signin")
    public ResponseEntity<?> signin(@RequestBody @Valid SigninRequest request) {
        return new ResponseEntity<>(authenticationService.signin(request), HttpStatus.OK);
    }

    @PatchMapping("/v0/auth")
    public ResponseEntity<?> confirm(@RequestParam @NotBlank @Size(max=36) String confirmCode){
        return new ResponseEntity<>(authenticationService.confirm(confirmCode),HttpStatus.OK);

    }

    @PatchMapping("/v1/user/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> changeRole(@PathVariable @Min(1) int id, @NotEmpty String[] auths){
        return new ResponseEntity<>(authenticationService.changeRole(id, auths), HttpStatus.OK);
    }
}