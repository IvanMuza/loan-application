package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {
    Mono<Boolean> existsById(Long id);
    Mono<ApplicationStatus> findByName(String name);
}
