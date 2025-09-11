package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class UserEmailNotFoundException extends BaseBusinessException {
    public UserEmailNotFoundException() {
        super(ErrorCodesEnum.USER_EMAIL_NOT_FOUND_404);
    }
}
