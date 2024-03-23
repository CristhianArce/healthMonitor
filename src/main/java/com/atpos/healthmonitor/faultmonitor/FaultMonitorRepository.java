package com.atpos.healthmonitor.faultmonitor;

import com.atpos.healthmonitor.models.DetectedFaults;
import org.springframework.data.repository.CrudRepository;

public interface FaultMonitorRepository extends CrudRepository<DetectedFaults, Long> {
}
