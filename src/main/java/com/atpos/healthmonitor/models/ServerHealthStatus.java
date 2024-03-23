package com.atpos.healthmonitor.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerHealthStatus {
    private String serverName;
    private boolean isHealthy;
}