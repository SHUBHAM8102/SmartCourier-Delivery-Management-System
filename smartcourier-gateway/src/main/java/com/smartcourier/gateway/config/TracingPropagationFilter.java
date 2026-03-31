package com.smartcourier.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class TracingPropagationFilter implements GlobalFilter, Ordered {

    private static final String B3_SINGLE_HEADER = "b3";
    private static final String X_B3_TRACEID = "X-B3-TraceId";
    private static final String X_B3_SPANID = "X-B3-SpanId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(X_B3_TRACEID);
        String spanId = request.getHeaders().getFirst(X_B3_SPANID);

        if (traceId != null && spanId != null) {
            log.debug("Propagating trace: traceId={}, spanId={}", traceId, spanId);
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
