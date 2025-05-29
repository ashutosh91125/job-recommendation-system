package com.jobrecommendation.apigateway.config;

import com.jobrecommendation.apigateway.filter.RateLimitFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final RateLimitFilter rateLimitFilter;

    public GatewayConfig(RateLimitFilter rateLimitFilter) {
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r
                        .path("/api/users/**")
                        .filters(f -> f.filter(rateLimitFilter.apply(new RateLimitFilter.Config())))
                        .uri("lb://user-service"))
                .route("job-posting-service", r -> r
                        .path("/api/jobs/**")
                        .filters(f -> f.filter(rateLimitFilter.apply(new RateLimitFilter.Config())))
                        .uri("lb://job-posting-service"))
                .route("recommendation-service", r -> r
                        .path("/api/recommendations/**")
                        .filters(f -> f.filter(rateLimitFilter.apply(new RateLimitFilter.Config())))
                        .uri("lb://recommendation-service"))
                .build();
    }
}
