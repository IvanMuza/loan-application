package co.com.loanapplications.api;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.dtos.LoanApplicationResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    beanClass = Handler.class,
                    beanMethod = "listenPostCreateLoanApplication",
                    operation = @Operation(
                            operationId = "createLoanApplication",
                            summary = "Create Loan Application",
                            description = "Create a new loan application with the provided data",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Loan application data",
                                    content = @Content(schema = @Schema(implementation = CreateLoanApplicationDto.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Loan application successfully created",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = LoanApplicationResponseDto.class))),
                                    @ApiResponse(responseCode = "404", description = "Loan type or user not found",
                                            content = @Content(mediaType = "application/json")),
                                    @ApiResponse(responseCode = "500", description = "Internal server error",
                                            content = @Content(mediaType = "application/json"))
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/loan-application",
                    beanClass = Handler.class,
                    beanMethod = "listLoanApplications",
                    operation = @Operation(
                            operationId = "listLoanApplications",
                            summary = "List loan applications for manual review",
                            description = "Returns a paged list of loan applications in statuses pending review / rejected / manual review. Requires role Consultant.",
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Page index (0-based)"),
                                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Page size"),
                                    @Parameter(name = "q", in = ParameterIn.QUERY, description = "Filter (email, loanType, status)")
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Paged result",
                                            content = @Content(mediaType = "application/json",
                                                    schema = @Schema(implementation = LoanApplicationResponseDto.class))),
                                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/loan-application"), handler::listenPostCreateLoanApplication)
                .and(route(GET("/api/v1/loan-application/list"), handler::listenGetListLoanApplications));
    }
}
