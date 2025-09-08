package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedStatusesEnum {
    ACTIVE("ACTIVE", "Active application"),
    PENDING_REVIEW("PENDING_REVIEW", "Application in pending review"),
    APPROVED("APPROVED", "Application approved"),
    REJECTED("REJECTED", "Application rejected"),
    MANUAL_REVIEW("MANUAL_REVIEW", "Manual review");

    private final String name;
    private final String description;
}
