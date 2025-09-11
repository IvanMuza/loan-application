package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class UserNotAuthorizedException extends BaseBusinessException {
    public UserNotAuthorizedException() {
        super(ErrorCodesEnum.USER_NOT_AUTHORIZED_401);
    }
}
