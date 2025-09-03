package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.r2dbc.entity.ApplicationStatusEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApplicationStatusReactiveRepository extends ReactiveCrudRepository<ApplicationStatusEntity, Long>,
        ReactiveQueryByExampleExecutor<ApplicationStatusEntity> {
}
