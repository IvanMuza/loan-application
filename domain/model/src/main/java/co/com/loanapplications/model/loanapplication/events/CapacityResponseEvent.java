package co.com.loanapplications.model.loanapplication.events;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacityResponseEvent {
    private Long applicationId;
    private String decision;
}
