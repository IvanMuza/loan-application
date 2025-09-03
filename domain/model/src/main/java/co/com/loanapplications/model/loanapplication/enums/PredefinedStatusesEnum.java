package co.com.loanapplications.model.loanapplication.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PredefinedStatusesEnum {
    PENDING_REVIEW(2L, "PENDING_REVIEW", "Pending review");

    private final Long id;
    private final String name;
    private final String description;
}
