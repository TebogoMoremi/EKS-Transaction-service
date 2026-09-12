package com.tebogo.eks.error;

public class IntegrationException extends RuntimeException {

    private final String errorCode;
    private final Object[] arguments;

    public IntegrationException(
            String errorCode,
            Throwable cause,
            Object... arguments) {

        super(errorCode, cause);

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