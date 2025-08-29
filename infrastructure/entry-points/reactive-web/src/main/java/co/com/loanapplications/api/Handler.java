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
//    private final CreateLoanApplicationUseCase useCase;
//    private final LoanApplicationMapper mapper;
//    private final TransactionalOperator tx;
//
    public Mono<ServerResponse> listenPostUseCase(ServerRequest serverRequest) {
//        return serverRequest.bodyToMono(CreateLoanApplicationDto.class)
//                .map(dto -> {
//                    LoanApplication base = mapper.toDomain(dto);
//                    return Tuples.of(base, dto.getLoanTypeId());
//                })
//                .flatMap(tuple -> useCase.createLoanApplication(tuple.getT1(), tuple.getT2()))
//                .map(mapper::toResponse)
//                .flatMap(resp -> ServerResponse.status(HttpStatus.CREATED)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(resp))
//                .as(tx::transactional);
        return ServerResponse.ok().bodyValue("");
    }
}
