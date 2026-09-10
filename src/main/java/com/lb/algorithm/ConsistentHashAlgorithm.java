package com.lb.algorithm;

import com.lb.backend.BackendInstance;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashAlgorithm implements LoadBalancerAlgorithm {
    private static final int VIRTUAL_NODES = 160;

    @Override
    public Optional<BackendInstance> selectBackend(List<BackendInstance> instances, String clientIp) {
        if(instances.isEmpty()) return Optional.empty();

        SortedMap<Integer, BackendInstance> ring = buildRing(instances);

        if(ring.isEmpty()) return Optional.empty();

        int hash = clientIp.hashCode();

        SortedMap<Integer, BackendInstance> tailMap = ring.tailMap(hash);
        int nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
        return Optional.of(ring.get(nodeHash));
    }


    private SortedMap<Integer, BackendInstance> buildRing(List<BackendInstance> instances) {
        SortedMap<Integer, BackendInstance> ring = new TreeMap<>();

        for(BackendInstance instance : instances) {
            for(int i = 0; i < VIRTUAL_NODES; i++) {
                String node = instance.getId() + "-" + i;
                int hash = node.hashCode();
                ring.put(hash, instance);
            }
        }

        return ring;
    }
}
