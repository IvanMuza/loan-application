package co.com.loanapplications.model.loanapplication;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private Long id;
    private String name;
    private Double minAmount;
    private Double maxAmount;
    private Boolean automaticValidation;
}
