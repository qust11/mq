package org.example.broker.config;

/**
 * @author qushutao
 * @since 2025-05-25
 */
public class GlobalProperties {

    private String mqHome;

    public GlobalProperties() {

    }

    public GlobalProperties(String mqHome) {
        this.mqHome = mqHome;
    }

    public String getMqHome() {
        return mqHome;
    }
}
