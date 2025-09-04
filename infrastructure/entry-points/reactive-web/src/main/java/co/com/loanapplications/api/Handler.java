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

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> listenPostCreateLoanApplication(ServerRequest serverRequest) {
        log.info("listenPostUseCase"); //TODO: WebFilter, improve logs
        return serverRequest.bodyToMono(CreateLoanApplicationDto.class)
                .flatMap(dto -> {
                    LoanApplication loanApp = loanApplicationMapper.toDomain(dto);
                    return createLoanApplicationUseCase.createLoanApplication(loanApp, dto.getLoanType());
                })
                .map(loanApplicationMapper::toResponse)
                .flatMap(loanApplicationResponseDto -> {
                    log.info("Successfully created loan application for user: {}", loanApplicationResponseDto.getEmail());
                    return ServerResponse
                            .status(HttpStatus.CREATED.value())
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(loanApplicationResponseDto);
                }).as(transactionalOperator::transactional);
    }
}
