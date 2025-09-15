package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class ApplicationStatusNotFoundException extends BaseBusinessException {
    public ApplicationStatusNotFoundException() {
        super(ErrorCodesEnum.APPLICATION_STATUS_APPROVED_NOT_FOUND_404);
    }
}
