package co.com.loanapplications.sqs.sender;

import co.com.loanapplications.model.loanapplication.events.CapacityRequestEvent;
import co.com.loanapplications.model.loanapplication.events.LoanApplicationStatusEvent;
import co.com.loanapplications.model.loanapplication.gateways.LoanApplicationStatusEventRepository;
import co.com.loanapplications.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Log4j2
@Service
@RequiredArgsConstructor
public class SQSSender implements LoanApplicationStatusEventRepository {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> publish(LoanApplicationStatusEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .map(body -> SendMessageRequest.builder()
                        .queueUrl(properties.publishQueueUrl())
                        .messageBody(body)
                        .build()
                )
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Message sent to SQS queue={} with messageId={}",
                        properties.publishQueueUrl(), response.messageId()))
                .then();
    }

    public Mono<Void> validate(CapacityRequestEvent event) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .map(body -> SendMessageRequest.builder()
                        .queueUrl(properties.validateQueueUrl())
                        .messageBody(body)
                        .build())
                .flatMap(req -> Mono.fromFuture(client.sendMessage(req)))
                .doOnNext(resp -> log.info("Capacity request sent messageId={}", resp.messageId()))
                .then();
    }

    public Mono<String> send(String message) {
        return Mono.fromCallable(() -> buildRequest(message, properties.publishQueueUrl()))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response ->
                        log.info("Sending message to SQS with ID: {}", response.messageId()))
                .map(SendMessageResponse::messageId);
    }

    private SendMessageRequest buildRequest(String message, String queueUrl) {
        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }
}
