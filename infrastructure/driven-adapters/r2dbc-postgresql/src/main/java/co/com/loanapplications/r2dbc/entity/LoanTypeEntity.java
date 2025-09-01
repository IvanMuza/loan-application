package co.com.loanapplications.r2dbc.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "loan_type")
public class LoanTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id")
    private Long id;

    @Column("name")
    private Double name;

    @Column("min_amount")
    private Double minAmount;

    @Column("maxAmount")
    private Double maxAmount;

    @Column("interest_rate")
    private BigDecimal interestRate;

    @Column("automatic_validation")
    private Boolean automaticValidation;
}
