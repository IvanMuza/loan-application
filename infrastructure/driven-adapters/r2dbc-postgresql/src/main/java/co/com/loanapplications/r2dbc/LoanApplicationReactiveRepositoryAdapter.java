package co.com.loanapplications.r2dbc;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import co.com.loanapplications.r2dbc.entity.LoanApplicationEntity;
import co.com.loanapplications.r2dbc.helper.ReactiveAdapterOperations;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Repository
public class LoanApplicationReactiveRepositoryAdapter
        extends ReactiveAdapterOperations<LoanApplication, LoanApplicationEntity, UUID, LoanApplicationReactiveRepository>
        implements LoanApplicationRepository {
    private final DatabaseClient client;

    public LoanApplicationReactiveRepositoryAdapter(LoanApplicationReactiveRepository repository,
                                                    ObjectMapper mapper,
                                                    R2dbcEntityTemplate template) {
        super(repository, mapper, d -> mapper.map(d, LoanApplication.class));
        this.client = template.getDatabaseClient();
    }

    @Override
    public Flux<LoanApplication> findByStatusNames(List<String> statusNames, int offset, int limit) {
        String sql = "SELECT * FROM loan_application la " +
                "JOIN application_status s ON la.status_id = s.id " +
                "WHERE s.name = ANY(:statuses) " +
                "ORDER BY la.id DESC " +
                "OFFSET :offset LIMIT :limit";
        return client.sql(sql)
                .bind("statuses", statusNames.toArray(new String[0]))
                .bind("offset", offset)
                .bind("limit", limit)
                .map(this::mapRowToLoanApplication)
                .all();
    }

    @Override
    public Mono<Long> countByStatusNames(List<String> statusNames) {
        String sql = "SELECT count(*) AS total FROM loan_application la " +
                "JOIN application_status s ON la.status_id = s.id " +
                "WHERE s.name = ANY(:statuses)";

        return client.sql(sql)
                .bind("statuses", statusNames.toArray(new String[0]))
                .map(row -> row.get("total", Long.class))
                .one();
    }

    private LoanApplication mapRowToLoanApplication(Row row, RowMetadata meta) {
        LoanApplication la = new LoanApplication();
        la.setId(row.get("id", Long.class));
        la.setAmount(row.get("amount", Double.class));
        la.setTermMonths(row.get("term_months", Integer.class));
        la.setEmail(row.get("email", String.class));
        la.setStatusId(row.get("status_id", Long.class));
        la.setLoanTypeId(row.get("loan_type_id", Long.class));
        return la;
    }

}
