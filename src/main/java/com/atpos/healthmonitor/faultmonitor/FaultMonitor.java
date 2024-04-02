package com.atpos.healthmonitor.faultmonitor;

import com.atpos.healthmonitor.models.DetectedFaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FaultMonitor {

    private final FaultMonitorRepository faultMonitorRepository;

    public FaultMonitor(FaultMonitorRepository faultMonitorRepository) {
        this.faultMonitorRepository = faultMonitorRepository;
    }

    @Transactional
    public void saveRetryAttempt(String serverName, String faultDescription, double elapsedTime, String severity){
        var fault = new DetectedFaults();
        fault.setServerName(serverName);
        fault.setFaultDescription(faultDescription);
        fault.setElapsedTime(elapsedTime);
        fault.setSeverity(severity);
        faultMonitorRepository.save(fault);
    }
}
