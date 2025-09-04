package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedStatusesEnum {
    ACTIVE("ACTIVE", "Active application"),
    PENDING_REVIEW("PENDING_REVIEW", "Pending review"),
    COMPLETED("COMPLETED", "Application completed");

    private final String name;
    private final String description;
}
