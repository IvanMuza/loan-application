package co.com.loanapplications.api.mappers;

import co.com.loanapplications.api.dtos.CreateLoanApplicationDto;
import co.com.loanapplications.api.dtos.LoanApplicationResponseDto;
import co.com.loanapplications.model.loanapplication.LoanApplication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanApplicationMapper {
    @Mapping(target = "loanTypeId", ignore = true)
    @Mapping(target = "statusId", ignore = true)
    @Mapping(target = "id", ignore = true)
    LoanApplication toDomain(CreateLoanApplicationDto request);

    LoanApplicationResponseDto toResponse(LoanApplication application);
}
