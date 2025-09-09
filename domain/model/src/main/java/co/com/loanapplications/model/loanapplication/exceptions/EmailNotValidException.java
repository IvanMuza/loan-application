package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class EmailNotValidException extends BaseBusinessException {
    public EmailNotValidException() {
        super(ErrorCodesEnum.EMAIL_INVALID_400);
    }
}
