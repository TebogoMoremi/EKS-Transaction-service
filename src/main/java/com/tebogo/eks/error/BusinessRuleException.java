package com.tebogo.eks.error;

public class BusinessRuleException extends RuntimeException {

    private final String errorCode;
    private final Object[] arguments;

    public BusinessRuleException(
            String errorCode,
            Object... arguments) {

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