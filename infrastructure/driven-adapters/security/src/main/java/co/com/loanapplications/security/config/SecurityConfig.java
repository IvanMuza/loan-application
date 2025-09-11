package co.com.loanapplications.security.config;

import co.com.loanapplications.model.loanapplication.exceptions.UserNotAuthenticatedException;
import co.com.loanapplications.model.loanapplication.exceptions.UserNotAuthorizedException;
import co.com.loanapplications.security.ReactiveJwtAuthenticationConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final ReactiveJwtAuthenticationConverter reactiveJwtAuthenticationConverter;

    @Bean
    public ReactiveJwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA384");
        return NimbusReactiveJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS384)
                .build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(reactiveJwtAuthenticationConverter))
                )

                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/v1/loan-application").hasRole("Customer")
                        .pathMatchers("/swagger-ui.html", "/v3/api-docs/**", "/webjars/swagger-ui/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/loan-application/list").hasRole("Consultant")
                        .anyExchange().authenticated()
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler((exchange, denied) ->
                                Mono.error(new UserNotAuthorizedException()))
                        .authenticationEntryPoint((exchange, e) ->
                                Mono.error(new UserNotAuthenticatedException())
                        )
                )
                .build();
    }
}
