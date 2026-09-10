package com.lb.backend;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class BackendInstance {
    private final String id;
    private final String host;
    private final int port;
    @Setter
    private int weight;
    @Setter
    private int failureCount;

    public String getUrl() {
        return String.format("http://%s:%d", host, port);
    }
}
