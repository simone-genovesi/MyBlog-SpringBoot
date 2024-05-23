package it.cgmconsulting.myblog.service;

import it.cgmconsulting.myblog.entity.Consent;
import it.cgmconsulting.myblog.entity.ConsentId;
import it.cgmconsulting.myblog.entity.User;
import it.cgmconsulting.myblog.entity.enumeration.Frequency;
import it.cgmconsulting.myblog.exception.ResourceNotFoundException;
import it.cgmconsulting.myblog.repository.ConsentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsentService {

    private final ConsentRepository consentRepository;

    public void save(Consent consent){
        consentRepository.save(consent);
    }

    public String updateConsent(UserDetails userDetails, Frequency frequency) {
        Consent consent = findConsentById(new ConsentId((User) userDetails));
        if(frequency.equals(Frequency.NEVER)){
            consent.setSendNewsletter(false);
            consent.setFrequency(frequency);
        }

        if(frequency.equals(Frequency.WEEKLY)){
            consent.setSendNewsletter(true);
            consent.setFrequency(frequency);
        }

        if(frequency.equals(Frequency.MONTHLY)){
            consent.setSendNewsletter(true);
            consent.setFrequency(frequency);
        }

        consentRepository.save(consent);

        return "Consent updated";
    }

    public List<Consent> getNewsletterConsent(){
        return consentRepository.getNewsletterConsent();
    }

    public List<Consent> getNewsletterMembers(){
        return consentRepository.findBySendNewsletterTrue();
    }

    public Consent findConsentById(ConsentId consentId){
        return consentRepository.findById(consentId).orElseThrow(
                () -> new ResourceNotFoundException("Consent", "id", consentId));
    }
}