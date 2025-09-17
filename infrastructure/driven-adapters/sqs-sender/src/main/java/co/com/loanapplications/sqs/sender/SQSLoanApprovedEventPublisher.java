package co.com.loanapplications.sqs.sender;

import co.com.loanapplications.model.loanapplication.events.LoanApprovedEvent;
import co.com.loanapplications.model.loanapplication.gateways.LoanApprovedEventPublisher;
import co.com.loanapplications.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Log4j2
@Service
@RequiredArgsConstructor
public class SQSLoanApprovedEventPublisher implements LoanApprovedEventPublisher {

    private final SqsAsyncClient client;
    private final SQSSenderProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(LoanApprovedEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .map(body -> SendMessageRequest.builder()
                        .queueUrl(properties.publishReportQueueUrl())
                        .messageBody(body)
                        .build()
                )
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent to SQS queue={} with messageId={}",
                        properties.publishReportQueueUrl(), response.messageId()))
                .then();
    }
}
