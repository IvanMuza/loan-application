package co.com.loanapplications.model.loanapplication.gateways;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import reactor.core.publisher.Mono;

public interface ApplicationStatusRepository {
    Mono<ApplicationStatus> findById(Long id);
}
