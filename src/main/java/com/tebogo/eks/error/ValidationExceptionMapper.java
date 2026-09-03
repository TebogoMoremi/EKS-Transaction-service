package com.tebogo.eks.error;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.ThreadContext;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper
        implements ExceptionMapper<ValidationException> {

    private final MessageResolver messageResolver =
            new MessageResolver();

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(
            ValidationException exception) {

        Locale locale = getLocale();

        String message =
                messageResolver.resolve(
                        exception.getErrorCode(),
                        locale,
                        exception.getArguments()
                );

        String correlationId =
                ThreadContext.get("correlationId");

        ErrorResponse errorResponse =
                new ErrorResponse(
                        Instant.now(),
                        correlationId,
                        exception.getErrorCode(),
                        message
                );

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(errorResponse)
                .build();
    }

    private Locale getLocale() {

        if (headers == null) {
            return Locale.ENGLISH;
        }

        List<Locale> languages =
                headers.getAcceptableLanguages();

        if (languages == null || languages.isEmpty()) {
            return Locale.ENGLISH;
        }

        return languages.get(0);
    }
}