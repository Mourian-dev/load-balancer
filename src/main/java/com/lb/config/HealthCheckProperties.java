package com.lb.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "load-balancer.health-check")
@Getter
@Setter
public class HealthCheckProperties {

    private ActiveHealthCheckConfig active;
    private PassiveHealthCheckConfig passive;

    @Getter
    @Setter
    public static class ActiveHealthCheckConfig {
        private boolean enabled = true;
        private int intervalSeconds = 10;
        private int timeoutSeconds = 5;
        private String healthEndpoint = "/health";

        public void validate() {
            if(intervalSeconds < 1) {
                throw new IllegalArgumentException("interval-seconds must be greater than 0");
            }

            if(timeoutSeconds < 1) {
                throw new IllegalArgumentException("timeout-seconds must be greater than 0");
            }

            if(timeoutSeconds >= intervalSeconds) {
                throw new IllegalArgumentException("interval-seconds must be greater than timeout-seconds");
            }
        }
    }

    @Getter
    @Setter
    public static class PassiveHealthCheckConfig {
        private boolean enabled = true;
        private int failureThreshold = 3;

        public void validate() {
            if(failureThreshold < 1) {
                throw new IllegalArgumentException("failure-threshold must be greater than 0");
            }
        }
    }

    public void validate() {
        active.validate();
        passive.validate();
    }
}
