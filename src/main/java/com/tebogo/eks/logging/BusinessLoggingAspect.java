package com.tebogo.eks.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class BusinessLoggingAspect {

    private static final Logger LOGGER =
            LogManager.getLogger(
                    BusinessLoggingAspect.class
            );

    @Around("@annotation(operation)")
    public Object logBusinessOperation(
            ProceedingJoinPoint joinPoint,
            BusinessOperation operation)
            throws Throwable {

        long startTime =
                System.currentTimeMillis();

        LOGGER.info(
                "START operation={} method={}",
                operation.value(),
                joinPoint.getSignature().toShortString()
        );

        try {

            Object result =
                    joinPoint.proceed();

            long duration =
                    System.currentTimeMillis()
                    - startTime;

            LOGGER.info(
                    "END operation={} method={} durationMs={}",
                    operation.value(),
                    joinPoint.getSignature().toShortString(),
                    duration
            );

            return result;

        } catch (Throwable exception) {

            long duration =
                    System.currentTimeMillis()
                    - startTime;

            LOGGER.error(
                    "FAILED operation={} method={} durationMs={} error={}",
                    operation.value(),
                    joinPoint.getSignature().toShortString(),
                    duration,
                    exception.getMessage(),
                    exception
            );

            throw exception;
        }
    }
}