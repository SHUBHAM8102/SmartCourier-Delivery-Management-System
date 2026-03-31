package com.smartcourier.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ZipkinAutoConfiguration.class)
public class ZipkinTracingConfig {

    @Value("${management.zipkin.tracing.endpoint:http://zipkin:9411/api/v2/spans}")
    private String zipkinEndpoint;

}
