package com.example.pocauth.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UtenteService utenteService;
    private final JwtUtils jwtUtils;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();
    private final UtilityService utilityService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String userEmail;

        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        final var authHeader = utilityService.getAuthToken(request.getHeader("Cookie")).orElseThrow(()->new RuntimeException(""));

        userEmail = jwtUtils.extractUsername(authHeader);
        log.debug("{}.doFilterInternal - USERNAME {} ", this.getClass().getName(), userEmail);
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var user = utenteService.loadUserByUsername(userEmail);
            if (user != null && Boolean.TRUE.equals(jwtUtils.validateToken(authHeader, userEmail))) {
                log.debug("{}.doFilterInternal - USERNAME {} TOKEN VALIDO.", this.getClass().getName(), userEmail);
                var authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
                var context = SecurityContextHolder.getContext();
                context.setAuthentication(new UsernamePasswordAuthenticationToken(authToken, null, Collections.emptyList()));
                securityContextRepository.saveContext(context, request, response);
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Escludi l'esecuzione del filtro per i percorsi specifici
        String path = request.getServletPath();
        return path.startsWith("/auth/authenticate") ||
                path.startsWith("/auth/valida") ||
                path.startsWith("/auth/verify2FA") ||
                path.startsWith("/socket/") ||
                path.startsWith("/surf") ||
                Objects.equals(request.getMethod(), "OPTIONS");
    }
}