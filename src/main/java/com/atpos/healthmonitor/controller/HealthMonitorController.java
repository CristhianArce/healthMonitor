package com.atpos.healthmonitor.controller;

import com.atpos.healthmonitor.models.ServerHealthStatus;
import com.atpos.healthmonitor.faultmonitor.FaultMonitor;
import com.atpos.healthmonitor.ping.PingSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;

@RestController
public class HealthMonitorController {

    private static final Logger logger = Logger.getLogger(HealthMonitorController.class.getName());
    private PingSender pingSender;


    HealthMonitorController(PingSender pingSender) {
        this.pingSender = pingSender;
    }

    @GetMapping("/ping")
    public ServerHealthStatus ping() {
        long startTime = System.nanoTime();
        var serverStatus = pingSender.ping();
        long endTime = System.nanoTime();
        if(!serverStatus.isHealthy()) logger.info("Fault Detected in: " + ((endTime - startTime)/1e6) + "ms" );
        return serverStatus;
    }
}
