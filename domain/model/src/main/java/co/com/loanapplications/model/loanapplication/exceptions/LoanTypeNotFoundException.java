package co.com.loanapplications.model.loanapplication.exceptions;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;

public class LoanTypeNotFoundException extends BaseBusinessException {
    public LoanTypeNotFoundException() {
        super(ErrorCodesEnum.LOAN_TYPE_NOT_FOUND_404);
    }
}
