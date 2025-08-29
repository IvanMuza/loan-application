package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedStatusesEnum {
    PENDING_REVIEW("PENDING_REVIEW", "Pending review");

    private final String name;
    private final String description;
}
