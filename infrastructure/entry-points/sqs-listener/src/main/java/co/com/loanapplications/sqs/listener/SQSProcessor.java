package co.com.loanapplications.sqs.listener;

import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.events.CapacityResponseEvent;
import co.com.loanapplications.model.loanapplication.gateways.ApplicationStatusRepository;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final ObjectMapper objectMapper;
    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationStatusRepository applicationStatusRepository;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.fromCallable(() -> objectMapper.readValue(message.body(), CapacityResponseEvent.class))
                .flatMap(event ->
                        applicationStatusRepository.findByName(event.getDecision())
                                .flatMap(status ->
                                        loanApplicationRepository.findById(event.getApplicationId())
                                                .flatMap(app -> {
                                                    LoanApplication updated = app.toBuilder()
                                                            .statusId(status.getId())
                                                            .build();
                                                    return loanApplicationRepository.save(updated);
                                                })
                                )
                )
                .doOnSuccess(v -> log.info("Processed SQS message for applicationId={}", message.messageId()))
                .doOnError(e -> log.error("Error processing SQS message with body={}", message.body(), e))
                .then();
    }
}
