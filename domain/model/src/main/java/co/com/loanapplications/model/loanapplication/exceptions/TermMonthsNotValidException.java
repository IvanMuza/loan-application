package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class TermMonthsNotValidException extends BaseBusinessException {
    public TermMonthsNotValidException() {
        super(ErrorCodesEnum.TERM_REQUIRED_400);
    }
}
