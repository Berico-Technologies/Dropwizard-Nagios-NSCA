package com.bericotech.nagios.nsca;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.jsendnsca.NagiosSettings;
import com.googlecode.jsendnsca.builders.NagiosSettingsBuilder;
import com.googlecode.jsendnsca.encryption.Encryption;
import org.hibernate.validator.constraints.NotEmpty;

public class NSCAConfiguration {

    @NotEmpty
    private String hostname;

    private int port = 5667;

    private String password = null;

    // Socket timeout when sending
    private int timeout = 10000;

    private int connectionTimeout = 5000;

    private String encryption = "NONE";

    private boolean enableLargeMessages = false;

    @JsonProperty
    public boolean getEnableLargeMessages() {
        return enableLargeMessages;
    }

    @JsonProperty
    public void setEnableLargeMessages(boolean enableLargeMessages) {
        this.enableLargeMessages = enableLargeMessages;
    }

    @JsonProperty
    public String getHostname() {

        return hostname;
    }

    @JsonProperty
    public int getPort() {

        return port;
    }

    @JsonProperty
    public int getTimeout() {

        return timeout;
    }

    @JsonProperty
    public int getConnectionTimeout() {

        return connectionTimeout;
    }

    @JsonProperty
    public String getEncryption() {

        return encryption;
    }

    @JsonProperty
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    @JsonProperty
    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @JsonProperty
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @JsonProperty
    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public NagiosSettings getNagiosSettings(){

        NagiosSettingsBuilder builder = new NagiosSettingsBuilder()
                .withConnectionTimeout(connectionTimeout)
                .withResponseTimeout(timeout)
                .withEncryption(Encryption.NONE)
                .withNagiosHost(hostname)
                .withPort(port);

        if (enableLargeMessages) builder.withLargeMessageSupportEnabled();

        if (password != null) {

            builder.withPassword(password);
        }
        else {

            builder.withNoPassword();
        }

        return builder.create();
    }
}