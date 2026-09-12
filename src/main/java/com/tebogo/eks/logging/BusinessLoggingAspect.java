package com.tebogo.eks.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class BusinessLoggingAspect {

    private static final Logger LOGGER =
            LogManager.getLogger(BusinessLoggingAspect.class);

    @Around("@annotation(operation)")
    public Object logBusinessOperation(
            ProceedingJoinPoint joinPoint,
            BusinessOperation operation) throws Throwable {

        String correlationId =
                ThreadContext.get("correlationId");

        String operationName =
                operation.value();

        long start = System.currentTimeMillis();

        LOGGER.info(
                "START operation={} method={} correlationId={}",
                operationName,
                joinPoint.getSignature().toShortString(),
                correlationId
        );

        try {

            Object result = joinPoint.proceed();

            long duration =
                    System.currentTimeMillis() - start;

            LOGGER.info(
                    "END operation={} method={} durationMs={} correlationId={}",
                    operationName,
                    joinPoint.getSignature().toShortString(),
                    duration,
                    correlationId
            );

            return result;

        } catch (Throwable exception) {

            long duration =
                    System.currentTimeMillis() - start;

            LOGGER.error(
                    "FAILED operation={} method={} durationMs={} correlationId={} error={}",
                    operationName,
                    joinPoint.getSignature().toShortString(),
                    duration,
                    correlationId,
                    exception.getMessage(),
                    exception
            );

            // IMPORTANT:
            // Preserve ValidationException,
            // BusinessRuleException,
            // IntegrationException, etc.
            throw exception;
        }
    }
}