package com.zhongbao.platform.service;

import com.zhongbao.platform.model.Host;
import com.zhongbao.platform.model.HostPool;
import org.springframework.stereotype.Service;

@Service
public class HostService {
    HostPool hostPool = HostPool.hostPool;


    public void addHost() {
        hostPool.addHost();
    }

    public void houseKeeping() {
        hostPool.hostPoolHouseKeeping();
    }

    //return -1 when the host does not exist
    public double calculateWorkingHour(String id, long untilWhen) {
        Host host = hostPool.getHostById(id);
        if (host == null)
            return -1;
        return host.calculateCurrentWorkingHour(untilWhen);
    }


    public void removeHost(String id) {
        hostPool.removeHost(id);
    }

    public void removeOneHost() {
        hostPool.removeHost();
    }
}
