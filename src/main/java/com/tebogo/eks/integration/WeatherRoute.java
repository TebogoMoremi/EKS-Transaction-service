package com.tebogo.eks.integration;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

public class WeatherRoute extends RouteBuilder {

    @Override
    public void configure() {

        from("direct:weather")

            .routeId("weather-route")

            .log(
                "Calling weather service for latitude=${body.latitude}, longitude=${body.longitude}"
            )

            .setHeader(
                Exchange.HTTP_METHOD,
                constant("GET")
            )

            .setHeader(
                Exchange.HTTP_QUERY,
                simple(
                    "latitude=${body.latitude}" +
                    "&longitude=${body.longitude}" +
                    "&current=temperature_2m,wind_speed_10m"
                )
            )

            .to(
                "https://api.open-meteo.com/v1/forecast"
                + "?throwExceptionOnFailure=true"
            )

            .convertBodyTo(String.class)

            .log(
                "Weather service response received"
            );
    }
}