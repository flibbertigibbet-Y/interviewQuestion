package com.zhongbao.platform.model;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeePool {
    private int initialEmployeeNumber = 30;
    private int poolSize = initialEmployeeNumber;
    private Map<Employee, Thread> employeeThreadMap = null;
    public static EmployeePool employeePool = new EmployeePool();

    private EmployeePool() {
    }

    public synchronized int getPoolSize() {
        return poolSize;
    }

    public synchronized void createEmployeePool() {
        if (employeeThreadMap == null) {
            employeeThreadMap = new ConcurrentHashMap<>();

            for (int i = 0; i < initialEmployeeNumber; i++) {
                Employee employee = new Employee("employee" + UUID.randomUUID().toString());
                Thread thread = new Thread(employee);
                employeeThreadMap.put(employee, thread);
                thread.start();
            }
        }
    }

    public synchronized Employee getEmployee(String id) {
        Optional<Employee> employee = employeeThreadMap.keySet().stream().filter(anEmployee -> anEmployee.getId().equals(id)).findFirst();
        if (employee.isPresent()) {
            return employee.get();
        }
        return null;
    }

    public synchronized void addEmployee() {
        Employee employee = new Employee("employee" + UUID.randomUUID().toString());
        Thread thread = new Thread(employee);
        employeeThreadMap.put(employee, thread);
        poolSize++;
        thread.start();
    }

    public synchronized void removeEmployee(String id) {
        Employee employee = getEmployee(id);
        if (employee != null) {
            Thread thread = employeeThreadMap.get(employee);
            employeeThreadMap.remove(employee);
            poolSize--;
            thread.interrupt();
        }
    }
}
