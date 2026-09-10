package com.lb.config;

import com.lb.algorithm.AlgorithmFactory;
import com.lb.algorithm.LoadBalancerAlgorithm;
import com.lb.backend.BackendInstance;
import com.lb.backend.ResourcePool;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.util.List;

public class LoadBalancerConfig {


    @Bean
    public LoadBalancerConfigurationProperties loadBalancerConfigurationProperties() {
        return new LoadBalancerConfigurationProperties();
    }

    @Bean
    public ResourcePool resourcePool(LoadBalancerConfigurationProperties props) {
        ResourcePool resourcePool = new ResourcePool();

        if(props.getInstances() != null) {
            for(LoadBalancerConfigurationProperties.BackupConfig config : props.getInstances()) {
                BackendInstance instance = new BackendInstance(config.getId(), config.getHost(), config.getPort());
                instance.setWeight(config.getWeight() != null ? config.getWeight() : 1);
                resourcePool.add(instance);
            }
        }

        return resourcePool;
    }

    @Bean
    public LoadBalancerAlgorithm loadBalancerAlgorithm(LoadBalancerConfigurationProperties props) {
        return AlgorithmFactory.createAlgorithm(props.getAlgorithm());
    }

    @Getter
    @Setter
    @ConfigurationProperties(prefix = "load-balancer")
    class LoadBalancerConfigurationProperties {
        private String algorithm;
        private List<BackupConfig> instances;

        @Getter
        @Setter
        static class BackupConfig {
            private String id;
            private String host;
            private int port;
            private Integer weight;
        }

    }
}
