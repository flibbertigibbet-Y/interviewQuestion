package com.zhongbao.platform.controller;

import com.zhongbao.platform.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @GetMapping(value = "/employee/add")
    public String addEmployee() {
        employeeService.addEmployee();
        return "added";
    }

    @DeleteMapping(value = "/employee/{id}")
    public String removeEmployee(@PathVariable String id) {
        employeeService.removeEmployee(id);
        return "removed";
    }

    @GetMapping(value = "/employee/{id}/info")
    public double calculateWorkingHour(@PathVariable String id) {
        long currentTime = System.currentTimeMillis();
        return employeeService.calculateWorkingHour(id, currentTime);
    }

}
