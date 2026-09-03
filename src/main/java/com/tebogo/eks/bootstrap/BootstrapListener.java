package com.tebogo.eks.bootstrap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class BootstrapListener
        implements ServletContextListener {

    private static final Logger LOGGER =
            LogManager.getLogger(
                    BootstrapListener.class
            );


    @Override
    public void contextInitialized(
            ServletContextEvent event) {

        LOGGER.info(
                "Starting EKS Transaction Service"
        );

        try {

            ApplicationContext.start();

            LOGGER.info(
                    "EKS Transaction Service started successfully"
            );

        } catch (Exception exception) {

            LOGGER.error(
                    "Failed to start EKS Transaction Service",
                    exception
            );

            throw new RuntimeException(
                    "Application startup failed",
                    exception
            );
        }
    }


    @Override
    public void contextDestroyed(
            ServletContextEvent event) {

        LOGGER.info(
                "Stopping EKS Transaction Service"
        );

        try {

            ApplicationContext.stop();

            LOGGER.info(
                    "EKS Transaction Service stopped successfully"
            );

        } catch (Exception exception) {

            LOGGER.error(
                    "Error while stopping application",
                    exception
            );
        }
    }
}