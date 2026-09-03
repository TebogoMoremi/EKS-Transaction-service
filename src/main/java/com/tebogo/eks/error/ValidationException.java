package com.tebogo.eks.error;

public class ValidationException extends RuntimeException {

    private final String errorCode;
    private final Object[] arguments;

    public ValidationException(String errorCode, Object... arguments) {
        super(errorCode);

        this.errorCode = errorCode;
        this.arguments = arguments;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getArguments() {
        return arguments;
    }
}