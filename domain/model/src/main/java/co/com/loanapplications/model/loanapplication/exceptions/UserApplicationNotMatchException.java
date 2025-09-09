package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class UserApplicationNotMatchException extends BaseBusinessException {
    public UserApplicationNotMatchException() {
        super(ErrorCodesEnum.USER_APPLICATION_NOT_MATCH_403);
    }
}
