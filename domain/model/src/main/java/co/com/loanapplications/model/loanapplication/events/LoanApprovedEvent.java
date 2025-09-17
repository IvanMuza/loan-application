package co.com.loanapplications.model.loanapplication.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApprovedEvent {
    Long loanId;
    String email;
    Double amount;
}
