package co.com.loanapplications.api.mappers;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.dtos.LoanApplicationResponseDto;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;
import org.springframework.web.bind.annotation.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    LoanApplication toDomain(CreateLoanApplicationDto dto);
    LoanApplicationResponseDto toResponse(LoanApplication application);
}
