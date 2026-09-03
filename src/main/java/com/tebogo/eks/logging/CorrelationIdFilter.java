package com.tebogo.eks.logging;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.ThreadContext;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorrelationIdFilter
        implements ContainerRequestFilter, ContainerResponseFilter {

    public static final String HEADER_NAME = "X-Correlation-ID";
    public static final String MDC_KEY = "correlationId";

    @Override
    public void filter(
            ContainerRequestContext requestContext)
            throws IOException {

        String correlationId =
                requestContext.getHeaderString(HEADER_NAME);

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ThreadContext.put(
                MDC_KEY,
                correlationId
        );
    }

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext)
            throws IOException {

        String correlationId =
                ThreadContext.get(MDC_KEY);

        if (correlationId != null) {

            responseContext
                    .getHeaders()
                    .putSingle(
                            HEADER_NAME,
                            correlationId
                    );
        }

        ThreadContext.remove(MDC_KEY);
    }
}