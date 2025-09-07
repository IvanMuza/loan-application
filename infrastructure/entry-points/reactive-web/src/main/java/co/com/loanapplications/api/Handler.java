package co.com.loanapplications.api;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.mappers.LoanApplicationMapper;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;
import co.com.loanapplications.model.loanapplication.exceptions.ForbiddenException;
import co.com.loanapplications.usecase.createloanapplication.CreateLoanApplicationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final CreateLoanApplicationUseCase createLoanApplicationUseCase;
    private final LoanApplicationMapper loanApplicationMapper;
    private final TransactionalOperator transactionalOperator;

    public Mono<ServerResponse> listenPostCreateLoanApplication(ServerRequest serverRequest) {
        return serverRequest.principal()
                .cast(JwtAuthenticationToken.class)
                .switchIfEmpty(Mono.error(new ForbiddenException(
                        ErrorCodesEnum.USER_NOT_AUTHORIZED.getCode(),
                        ErrorCodesEnum.USER_NOT_AUTHORIZED.getDefaultMessage()
                )))
                .flatMap(auth -> {
                    String requesterEmail = auth.getToken().getSubject();
                    return serverRequest.bodyToMono(CreateLoanApplicationDto.class)
                            .flatMap(dto -> {
                                if (requesterEmail == null ||
                                        dto.getEmail() == null ||
                                        !requesterEmail.equalsIgnoreCase(dto.getEmail())) {
                                    return Mono.error(new ForbiddenException(
                                            ErrorCodesEnum.USER_APPLICATION_NOT_MATCH.getCode(),
                                            ErrorCodesEnum.USER_APPLICATION_NOT_MATCH.getDefaultMessage()));
                                }
                                LoanApplication loanApp = loanApplicationMapper.toDomain(dto);
                                return createLoanApplicationUseCase
                                        .createLoanApplication(loanApp, dto.getLoanType());
                            });
                })
                .map(loanApplicationMapper::toResponse)
                .flatMap(resp -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp))
                .as(transactionalOperator::transactional);
    }
}
