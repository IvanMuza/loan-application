package co.com.loanapplications.api.dtos;

import lombok.Data;

@Data
public class LoanApplicationResponseDto {
    private String email;
    private Double amount;
    private Integer termMonths;
}
