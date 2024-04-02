package com.atpos.healthmonitor.ping;

import com.atpos.healthmonitor.faultmonitor.FaultMonitor;
import com.atpos.healthmonitor.feign.ATPosRestClient;
import com.atpos.healthmonitor.models.ServerHealthStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

@Service
public class PingSender {

    private final ATPosRestClient atPosRestClient;
    private static final Logger logger = Logger.getLogger(PingSender.class.getName());
    private static final int defaultNumberOfPackages = 1;

    @Value("${atpos.backend.url}")
    private String stringURL;

    @Value("${atpos.backend.port}")
    private String port;

    @Value("${atpos.backend.packets}")
    private int packets;

    @Value("${atpos.backend.waitingTime}")
    private double maxWaitingTime;
    private double timeInterval;
    private FaultMonitor faultMonitor;


    public PingSender(ATPosRestClient atPosRestClient, FaultMonitor faultMonitor) {
        this.atPosRestClient = atPosRestClient;
        this.faultMonitor = faultMonitor;
    }

    public ServerHealthStatus ping(){
        try {
            URL url = new URL(stringURL);
            String host = url.getHost();
            if (isReachableByPing(host, packets)) {
                try {
                    long backendRequestStartTime = System.nanoTime();
                    var ATPosServerResponse = atPosRestClient.getServerStatus();
                    long backendRequestEndTime = System.nanoTime();
                    double elapsedTime = (backendRequestEndTime - backendRequestStartTime) / 1e6;
                    if (elapsedTime > maxWaitingTime || ATPosServerResponse.getState().equals("DOWN")) {
                        keepRecord(1, elapsedTime);
                        return new ServerHealthStatus(stringURL, false);
                    }
                    return new ServerHealthStatus(stringURL, true);
                } catch (Exception e) {
                    logger.severe("The endpoint /health is not available ... will re-try");
                    keepRecord(2, 0);
                    return new ServerHealthStatus(stringURL, false);
                }
            } else {
                logger.severe("The required server is not available ... will re-try");
                keepRecord(3, 0);
                return new ServerHealthStatus(stringURL, false);
            }
        } catch (MalformedURLException e) {
            logger.warning("The provided url " + stringURL + " does not have a valid format.");
            throw new IllegalArgumentException("PingSender Server is not properly configured");
        }
    }

    private void keepRecord(int message, double elapsedTime){
        var messageDescription = "No error description provided";
        if (message == 1) messageDescription = "The given endpoint is not responding in the expected time span";
        if (message == 2) messageDescription = "The given endpoint is not reachable";
        if (message == 3) messageDescription = "The required server is not reachable";
        faultMonitor.saveRetryAttempt(
                stringURL,
                messageDescription,
                elapsedTime,
                "WARNING");
    }

    private boolean isReachableByPing(String host, int packets) {
        try{
            String cmd;
            int numberOfPackets = Math.max(packets, defaultNumberOfPackages);
            if(System.getProperty("os.name").startsWith("Windows")) {
                cmd = String.format("ping -n %s %s", numberOfPackets, host);
            } else {
                cmd = String.format("ping -c %s %s", numberOfPackets, host);
            }
            logger.info("Trying to reach server: " + cmd);
            Process myProcess = Runtime.getRuntime().exec(cmd);
            myProcess.waitFor();
            var succeededPing = myProcess.exitValue() == 0;
            if (succeededPing) logger.info("The required server " + host + " is up and running.");
            return succeededPing;
        } catch(Exception e) {
            logger.severe("The wanted host is not reachable.");
            return false;
        }
    }
}
