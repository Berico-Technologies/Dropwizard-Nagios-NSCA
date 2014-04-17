package com.bericotech.nagios.nsca;

import com.googlecode.jsendnsca.Level;
import com.googlecode.jsendnsca.MessagePayload;
import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import com.yammer.metrics.core.HealthCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A more specific version of the HealthCheck that allows a NSCA Message Payload to be
 * returned as the status of a health check.  Alternatively, the health check can still be
 * used in the standard Dropwizard way.
 */
public abstract class NagiosHealthCheck extends HealthCheck {

    private static final Logger logger = LoggerFactory.getLogger(NagiosHealthCheck.class);

    /**
     * Create a new {@link com.yammer.metrics.core.HealthCheck} instance with the given name.
     *
     * @param name the name of the health check (and, ideally, the name of the underlying
     *             component the health check tests)
     */
    protected NagiosHealthCheck(String name) { super(name); }

    /**
     * Classic Dropwizard Health Check, except, instead of requiring developers to implement both
     * the Dropwizard and Nagios NSCA check, we will translate the Nagios check into Dropwizard's
     * simpler format.
     * @return Translated Health Check (from Nagios to Dropwizard)
     * @throws Exception
     */
    @Override
    protected Result check() throws Exception {

        MessagePayload messagePayload = null;

        try {

            messagePayload = performCheck();

        } catch (Throwable t){

            logger.error(String.format("Could not determine status of '%s' health check.", getName()), t);

            return Result.unhealthy(t);
        }

        boolean isHealthy = messagePayload.getLevel().equals(Level.OK);

        if (isHealthy){

            logger.debug("Health Check '{}' returned healthy.", getName());

            return Result.healthy(messagePayload.getMessage());
        }

        logger.warn("Health Check '{}' returned unhealthy.", getName());

        return Result.unhealthy(messagePayload.getMessage());
    }

    MessagePayload performCheck(){

        MessagePayloadBuilder builder = MessagePayloadBuilderFactory.createBuilder(getName());

        return this.check(builder);
    }

    /**
     * Given a partially initialized MessagePayloadBuilder, return a MessagePayload.
     * You do not have to use the builder if you don't want, but it is pre-configured,
     * to specify the hostname and the service check name.
     * @param mpBuilder MessagePayloadBuilder.
     * @return
     */
    public abstract MessagePayload check(MessagePayloadBuilder mpBuilder);
}
