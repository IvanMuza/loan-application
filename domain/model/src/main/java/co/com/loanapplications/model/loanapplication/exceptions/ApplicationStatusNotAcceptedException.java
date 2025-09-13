package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class ApplicationStatusNotAcceptedException extends BaseBusinessException {
    public ApplicationStatusNotAcceptedException() {
        super(ErrorCodesEnum.APPLICATION_STATUS_NOT_ACCEPTED_406);
    }
}
