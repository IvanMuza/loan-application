package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.identity.UserDto;
import reactor.core.publisher.Mono;

public interface IdentityRepository {
    Mono<Boolean> emailExists(String email);
    Mono<UserDto> findByEmail(String email);
}
