package com.atpos.healthmonitor.feign;

import com.atpos.healthmonitor.utils.ATPosServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "ATPosClient", url = "${atpos.backend.url}", configuration = ClientConfiguration.class)
public interface ATPosRestClient {

    @RequestMapping(method = RequestMethod.GET, value = "/health", produces = "application/json")
    ATPosServerResponse getServerStatus();
}