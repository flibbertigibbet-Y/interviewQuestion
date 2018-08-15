package com.zhongbao.platform.controller;

import com.zhongbao.platform.service.HostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HostController {

    @Autowired
    private HostService hostService;


    @GetMapping(value = "/host/add")
    public String addHost() {
        hostService.addHost();
        return "added";
    }

    @GetMapping(value = "/host/remove/{id}")
    public String removeHost(@PathVariable String id) {
        hostService.removeHost(id);
        return "deleted";
    }

    @DeleteMapping(value = "/host")
    public String removeOneHost() {
        hostService.removeOneHost();
        return "deleted";
    }

    @GetMapping(value = "/host/{id}/info")
    public double calculateWorkingHour(@PathVariable String id) {
        long currentTime = System.currentTimeMillis();
        return hostService.calculateWorkingHour(id, currentTime);
    }

    @GetMapping(value = "/host/housekeeping")
    public String calculateWorkingHour() {
        hostService.houseKeeping();
        return "done";
    }

}
