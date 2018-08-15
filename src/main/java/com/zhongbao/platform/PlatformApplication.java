package com.zhongbao.platform;

import com.zhongbao.platform.model.EmployeePool;
import com.zhongbao.platform.model.HostPool;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlatformApplication {

    public static void main(String[] args) {

        SpringApplication.run(PlatformApplication.class, args);

        HostPool.hostPool.createHostPool();

        EmployeePool.employeePool.createEmployeePool();

    }
}
