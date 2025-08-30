package co.com.loanapplications.api;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.mappers.LoanApplicationMapper;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.usecase.createloanapplication.CreateLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;
    private final TransactionalOperator transactionalOperator;
//
    public Mono<ServerResponse> listenPostUseCase(ServerRequest serverRequest) {
        log.info("listenPostUseCase");
        return serverRequest.bodyToMono(CreateLoanApplicationDto.class)
                .map(loanApplicationMapper::toDomain)
                .flatMap(createLoanApplicationUseCase::createLoanApplication)
                .map(loanApplicationMapper::toResponse)
                .flatMap(loanApplicationResponse -> {
                    log.info("Successfully created user: {}", loanApplicationResponse.getClass());
                    return ServerResponse
                            .status(HttpStatus.CREATED.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(loanApplicationResponse);
                }).as(transactionalOperator::transactional);
    }
}
