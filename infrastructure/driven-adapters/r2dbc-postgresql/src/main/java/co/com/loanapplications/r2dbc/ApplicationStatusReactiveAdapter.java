package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.ApplicationStatus;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.r2dbc.entity.ApplicationStatusEntity;
import co.com.loanapplications.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ApplicationStatusReactiveAdapter
        extends ReactiveAdapterOperations<ApplicationStatus, ApplicationStatusEntity, Long, ApplicationStatusReactiveRepository>
        implements ApplicationStatusRepository {
    public ApplicationStatusReactiveAdapter(ApplicationStatusReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, ApplicationStatus.class));
    }

    @Override
    public Mono<ApplicationStatus> findById(Long id){
        return this.repository.findById(id)
                .map(entity -> mapper.map(entity, ApplicationStatus.class));
    }
}