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
public class IntegrationExceptionMapper
        implements ExceptionMapper<IntegrationException> {

    private final MessageResolver messageResolver =
            new MessageResolver();

    @Context
    private HttpHeaders headers;

    @Override
    public Response toResponse(
            IntegrationException exception) {

        Locale locale = getLocale();

        String message =
                messageResolver.resolve(
                        exception.getErrorCode(),
                        locale,
                        exception.getArguments()
                );

        ErrorResponse response =
                new ErrorResponse(
                        Instant.now().toString(),
                        ThreadContext.get("correlationId"),
                        exception.getErrorCode(),
                        message
                );

        return Response
                .status(Response.Status.BAD_GATEWAY)
                .entity(response)
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