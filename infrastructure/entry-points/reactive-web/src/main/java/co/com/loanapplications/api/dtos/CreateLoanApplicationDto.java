package co.com.loanapplications.api.dtos;

import lombok.Data;

@Data
public class CreateLoanApplicationDto {
    private String email;
    private Double amount;
    private Integer termMonths;
    private String loanType;
}
