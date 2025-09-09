package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class UserNotAuthenticatedException extends BaseBusinessException {
    public UserNotAuthenticatedException() {
        super(ErrorCodesEnum.USER_NOT_AUTHENTICATED_401);
    }
}
