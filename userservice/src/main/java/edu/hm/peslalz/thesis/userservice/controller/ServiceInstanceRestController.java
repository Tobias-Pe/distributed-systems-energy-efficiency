package edu.hm.peslalz.thesis.userservice.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Log4j2
public class ServiceInstanceRestController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/service-instances/{applicationName}")
    public List<ServiceInstance> serviceInstancesByApplicationName(
            @PathVariable String applicationName) {
        log.info("Request handling...");
        busyWork(200);
        return this.discoveryClient.getInstances(applicationName);
    }

    public static void busyWork(int durationInMilliseconds) {
        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime) < durationInMilliseconds) {
            // Leere Schleife, um Zeit zu verschwenden
        }
    }
}
