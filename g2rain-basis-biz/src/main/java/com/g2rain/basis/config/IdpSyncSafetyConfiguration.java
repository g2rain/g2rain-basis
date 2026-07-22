package com.g2rain.basis.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(IdpSyncSafetyProperties.class)
public class IdpSyncSafetyConfiguration {
}
