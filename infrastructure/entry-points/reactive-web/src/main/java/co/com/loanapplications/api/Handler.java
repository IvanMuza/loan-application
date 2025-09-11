package co.com.loanapplications.api;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.mappers.LoanApplicationMapper;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import co.com.loanapplications.model.loanapplication.exceptions.UserApplicationNotMatchException;
import co.com.loanapplications.model.loanapplication.exceptions.UserNotAuthorizedException;
import co.com.loanapplications.usecase.createloanapplication.CreateLoanApplicationUseCase;
import co.com.loanapplications.usecase.createloanapplication.ListLoanApplicationsUseCase;
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
    private final ListLoanApplicationsUseCase listLoanApplicationsUseCase;

    public Mono<ServerResponse> listenPostCreateLoanApplication(ServerRequest serverRequest) {
        return serverRequest.principal()
                .cast(JwtAuthenticationToken.class)
                .switchIfEmpty(Mono.error(new UserNotAuthorizedException()))
                .flatMap(auth -> {
                    String requesterEmail = auth.getToken().getSubject();
                    return serverRequest.bodyToMono(CreateLoanApplicationDto.class)
                            .flatMap(dto -> {
                                if (requesterEmail == null ||
                                        dto.getEmail() == null ||
                                        !requesterEmail.equalsIgnoreCase(dto.getEmail())) {
                                    return Mono.error(new UserApplicationNotMatchException());
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

    public Mono<ServerResponse> listenGetListLoanApplications(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String statusFilter = request.queryParam("statusFilter").orElse(null);

        return listLoanApplicationsUseCase.list(page, size, statusFilter)
                .flatMap(dto ->
                        ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(dto));
    }
}
