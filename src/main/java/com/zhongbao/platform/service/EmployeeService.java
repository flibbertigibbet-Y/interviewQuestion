package com.zhongbao.platform.service;

import com.zhongbao.platform.model.Employee;
import com.zhongbao.platform.model.EmployeePool;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {
    EmployeePool employeePool = EmployeePool.employeePool;

    public void addEmployee() {
        employeePool.addEmployee();
    }

    //return -1 when the employee does not exist
    public double calculateWorkingHour(String id, long untilWhen) {
        Employee employee = employeePool.getEmployee(id);
        if (employee != null) {
            return employee.calculateCurrentWorkingHour(untilWhen);
        }
        return -1;
    }

    public void removeEmployee(String id) {
        Employee employee = employeePool.getEmployee(id);

        if (employee != null) {
            employeePool.removeEmployee(id);
        } else {
            return;
        }
    }
}
