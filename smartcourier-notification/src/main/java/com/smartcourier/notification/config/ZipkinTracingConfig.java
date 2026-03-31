package com.smartcourier.notification.config;

import org.springframework.boot.actuate.autoconfigure.tracing.zipkin.ZipkinAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ZipkinAutoConfiguration.class)
public class ZipkinTracingConfig {
}
