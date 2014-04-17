package com.bericotech.nagios.nsca;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;
import com.googlecode.jsendnsca.NagiosPassiveCheckSender;
import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import com.yammer.dropwizard.Bundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.config.HealthCheckProvider;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NSCABundle implements Bundle {

    private static Logger logger = LoggerFactory.getLogger(NSCABundle.class);

    final NagiosSettings settings;

    HealthCheckSender healthCheckSender;

    ScheduledExecutorService executorService;

    int initialDelay = 120;
    int checkInterval = 60;
    TimeUnit timeUnit = TimeUnit.SECONDS;


    public NSCABundle(NSCAConfiguration configuration){

        this.settings = configuration.getNagiosSettings();
    }

    public void setTimeUnit(TimeUnit timeUnit) {

        this.timeUnit = timeUnit;
    }

    public void setInitialDelay(int initialDelay) {

        this.initialDelay = initialDelay;
    }

    public void setCheckInterval(int checkInterval) {

        this.checkInterval = checkInterval;
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {}

    @Override
    public void run(Environment environment) {

        healthCheckSender =
                new HealthCheckSender(new HealthCheckProvider(environment), new NagiosPassiveCheckSender(settings));

        executorService = Executors.newScheduledThreadPool(3);

        executorService.scheduleWithFixedDelay(healthCheckSender, initialDelay, checkInterval, timeUnit);
    }

    public static class HealthCheckSender implements Runnable {

        private static Logger logger = LoggerFactory.getLogger(HealthCheckSender.class);

        HealthCheckProvider healthCheckProvider;
        NagiosPassiveCheckSender nagiosAlerter;

        public HealthCheckSender(HealthCheckProvider healthCheckProvider, NagiosPassiveCheckSender nagiosAlerter){

            this.healthCheckProvider = healthCheckProvider;
            this.nagiosAlerter = nagiosAlerter;
        }

        @Override
        public void run() {

            logger.info("Running Health Checks.");

            Set<HealthCheck> healthChecks = this.healthCheckProvider.getHealthChecks();

            for (HealthCheck healthCheck : healthChecks){

                MessagePayload messagePayload = null;

                if (NagiosHealthCheck.class.isAssignableFrom(healthCheck.getClass())){

                    messagePayload = ((NagiosHealthCheck) healthCheck).performCheck();
                }
                else {

                    HealthCheck.Result result = healthCheck.execute();

                    MessagePayloadBuilder builder = MessagePayloadBuilderFactory.createBuilder(healthCheck.getName());

                    builder
                        .withMessage(result.getMessage())
                        .withLevel((result.isHealthy())? Level.OK : Level.CRITICAL);

                    messagePayload = builder.create();
                }

                try {

                    nagiosAlerter.send(messagePayload);

                    logger.debug("Health Check '{}' send to Nagios.", healthCheck.getName());

                } catch (Throwable e) {

                    logger.error("Failed to send Health Check to Nagios.", e);
                }
            }
        }
    }
}
