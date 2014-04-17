package com.yammer.dropwizard.config;

import com.yammer.metrics.core.HealthCheck;

import java.util.Set;

/**
 * This is in the Dropwizard namespace so
 * it can get access to Package-private
 * methods of Environment.
 */
public class HealthCheckProvider {

    Environment environment;

    public HealthCheckProvider(Environment environment) {

        this.environment = environment;
    }

    public Set<HealthCheck> getHealthChecks(){

        return this.environment.getHealthChecks();
    }
}