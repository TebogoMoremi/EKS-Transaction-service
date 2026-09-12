package com.tebogo.eks.error;

import java.time.Instant;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper
        implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER =
            LogManager.getLogger(
                    GlobalExceptionMapper.class
            );

    private final MessageResolver messageResolver =
            new MessageResolver();

    @Override
    public Response toResponse(
            Throwable exception) {

        String correlationId =
                ThreadContext.get(
                        "correlationId"
                );

        LOGGER.error(
                "Unexpected application error correlationId={}",
                correlationId,
                exception
        );

        String message =
                messageResolver.resolve(
                        "internal.error",
                        Locale.ENGLISH
                );

        ErrorResponse response =
                new ErrorResponse(
                        Instant.now().toString(),
                        correlationId,
                        "internal.error",
                        message
                );

        return Response
                .status(
                        Response.Status.INTERNAL_SERVER_ERROR
                )
                .entity(response)
                .build();
    }
}