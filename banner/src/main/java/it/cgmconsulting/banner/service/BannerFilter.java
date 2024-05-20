package it.cgmconsulting.banner.service;

import it.cgmconsulting.banner.entity.Campaign;
import it.cgmconsulting.banner.repository.CampaignRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BannerFilter extends OncePerRequestFilter {

    @Value("${application.token}")
    private String applicationToken;

    private final CampaignRepository campaignRepository;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String token = request.getHeader("Authorization");
        // controllo token per tutte le chiamate dell'applicazione
        if (!request.getServletPath().contains("/api")) {
            if (token == null || !token.equals(applicationToken)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return ;
            }
        } else {
            // controllo token per tutte le chiamate al banner da parte di clienti
            if (token == null || validateToken(token).isEmpty()) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private Optional<Campaign> validateToken(String token) {
        return campaignRepository.findById(token);
    }
}
