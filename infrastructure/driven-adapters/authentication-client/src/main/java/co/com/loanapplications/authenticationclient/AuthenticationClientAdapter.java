package co.com.loanapplications.authenticationclient;

import co.com.loanapplications.authenticationclient.config.AuthenticationClientProperties;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthenticationClientAdapter implements IdentityRepository {
    private final WebClient.Builder webClientBuilder;
    private final AuthenticationClientProperties props;

    @Override
    public Mono<Boolean> emailExists(String email) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .map(JwtAuthenticationToken::getToken)
                .map(Jwt::getTokenValue)
                .flatMap(token -> webClientBuilder.build()
                        .get()
                        .uri(uriBuilder -> uriBuilder
                                .path(props.getExistsPath())
                                .build(email))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .retrieve()
                        .bodyToMono(Boolean.class)
                )
                .onErrorResume(e -> {
                    return Mono.just(false);
                });
    }
}
