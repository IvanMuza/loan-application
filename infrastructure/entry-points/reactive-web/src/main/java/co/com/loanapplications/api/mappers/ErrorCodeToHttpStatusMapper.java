package co.com.loanapplications.api.mappers;

import co.com.loanapplications.model.loanapplication.enums.ErrorCodesEnum;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
public class ErrorCodeToHttpStatusMapper {
    public HttpStatus map(ErrorCodesEnum errorCode) {
        return switch (errorCode) {
            case APPLICATION_REQUIRED_400,
                 AMOUNT_REQUIRED_400,
                 TERM_REQUIRED_400,
                 EMAIL_REQUIRED_400,
                 DOCUMENT_ID_REQUIRED_400,
                 LOAN_TYPE_REQUIRED_400,
                 AMOUNT_OUT_OF_RANGE_400,
                 EMAIL_INVALID_400,
                 USER_APPLICATION_NOT_MATCH_404 -> HttpStatus.BAD_REQUEST;
            case USER_NOT_AUTHORIZED_401,
                 USER_NOT_AUTHORIZED_TO_CREATE_401 -> HttpStatus.UNAUTHORIZED;
            case LOAN_TYPE_NOT_FOUND_404,
                 USER_EMAIL_NOT_FOUND_404 -> HttpStatus.NOT_FOUND;
            case USER_NOT_AUTHENTICATED_403 -> HttpStatus.FORBIDDEN;
        };
    }
}
