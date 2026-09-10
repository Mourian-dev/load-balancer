package com.lb.backend;

import java.util.ArrayList;
import java.util.List;

public class ResourcePool {
    private final List<BackendInstance> instances;

    public ResourcePool() {
        instances = new ArrayList<>();
    }

    public void add(BackendInstance instance) {
        instances.add(instance);
    }

    public void remove(String id) {
        instances.removeIf(i -> i.getId().equals(id));
    }

    public List<BackendInstance> getAll() {
        return new ArrayList<>(instances);
    }
}
