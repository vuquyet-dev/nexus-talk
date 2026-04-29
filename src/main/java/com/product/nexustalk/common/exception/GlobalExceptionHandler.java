package com.product.nexustalk.common.exception;

import com.product.nexustalk.common.api.BaseResponse;
import com.product.nexustalk.auth.exception.InvalidCredentialsException;
import com.product.nexustalk.auth.exception.InvalidRefreshTokenException;
import com.product.nexustalk.config.ErrorConf;
import com.product.nexustalk.user.exception.DuplicateUserException;
import com.product.nexustalk.user.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private HttpStatus status;

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BaseResponse> handleUserNotFound(UserNotFoundException ex) {
        status = HttpStatus.NOT_FOUND;
        return ResponseEntity.status(status)
                .body(BaseResponse.of(status.value(), status.getReasonPhrase(),ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<BaseResponse> handleDuplicate(DuplicateUserException ex) {
        status = HttpStatus.CONFLICT;
        return ResponseEntity.status(status)
                .body(BaseResponse.of(status.value(), status.getReasonPhrase(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleValidation(MethodArgumentNotValidException ex) {
        status = HttpStatus.BAD_REQUEST;
        String message = ex.getBindingResult().getAllErrors().stream()
                .findFirst()
                .map(err -> {
                    if (err instanceof FieldError fe) {
                        return fe.getField() + ": " + fe.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .orElse("Validation error");
        return ResponseEntity.badRequest()
                .body(BaseResponse.of(status.value(), status.getReasonPhrase(), message));
    }

    @ExceptionHandler(InvalidCredentialsException.class)

    public ResponseEntity<BaseResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(status)
                .body(BaseResponse.of(status.value(), ErrorConf.getInstance().DES_401_INVALID_CREDENTIALS, ex.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<BaseResponse> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        status = HttpStatus.UNAUTHORIZED;
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(BaseResponse.of(status.value(), status.getReasonPhrase(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleFallback(Exception ex) {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(BaseResponse.of(status.value(), status.getReasonPhrase(), "Unexpected error"));
    }
}
