package co.com.loanapplications.authenticationclient;

import co.com.loanapplications.authenticationclient.config.AuthenticationClientProperties;
import co.com.loanapplications.model.loanapplication.gateways.IdentityRepository;
import lombok.RequiredArgsConstructor;
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
        return webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(props.getExistsPath())
                        .build(email))
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(e -> {
                    return Mono.just(false);
                });
    }
}
