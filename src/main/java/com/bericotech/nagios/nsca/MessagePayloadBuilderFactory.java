package com.bericotech.nagios.nsca;

import com.googlecode.jsendnsca.builders.MessagePayloadBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;

/**
 * Stupid Factory to centralize the configuration of MessagePayloadBuilders used by multiple components.
 */
public class MessagePayloadBuilderFactory {

    private static Logger logger = LoggerFactory.getLogger(MessagePayloadBuilderFactory.class);

    static String DEFAULT_HOSTNAME = "localhost";

    static {

        try {

            DEFAULT_HOSTNAME = Inet4Address.getLocalHost().getHostName();

        } catch (Exception e){

            logger.error("Could not resolve hostname.", e);
        }
    }

    public static MessagePayloadBuilder createBuilder(String serviceName){

        return new MessagePayloadBuilder()
                .withHostname(DEFAULT_HOSTNAME)
                .withServiceName(serviceName);
    }
}
