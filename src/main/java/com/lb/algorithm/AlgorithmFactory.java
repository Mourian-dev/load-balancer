package com.lb.algorithm;

public class AlgorithmFactory {
    public static LoadBalancerAlgorithm createAlgorithm(String algorithm) {
        if(algorithm == null || algorithm.isBlank()) algorithm = "round-robin";

        return switch(algorithm.toLowerCase()) {
            case "round-robin" -> new RoundRobinAlgorithm();
            case "weighted" -> new WeightedRoundRobinAlgorithm();
            case "random" -> new RandomAlgorithm();
            case "ip-hash" -> new IpHashAlgorithm();
            case "consistent-hashing" -> new ConsistentHashAlgorithm();
            default -> throw new IllegalArgumentException("Unknown algorithm: " + algorithm);
        };
    }
}
