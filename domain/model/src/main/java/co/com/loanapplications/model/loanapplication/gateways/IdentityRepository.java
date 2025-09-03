package co.com.loanapplications.model.loanapplication.gateways;

import reactor.core.publisher.Mono;

public interface IdentityRepository {
    Mono<Boolean> emailExists(String email);
}
