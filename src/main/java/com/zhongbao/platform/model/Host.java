package com.zhongbao.platform.model;

import com.zhongbao.platform.util.TimeTransformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Host implements Subject, Accountable {
    volatile private boolean toBeRemoved;

    private static final int workingHour = 5000;//in Millis(It should be hour, but for my benefit, I just put milli sec here)
    private double totalWorkingHour;
    private long currentWorkStartsAt;
    private String id;
    private List<Observer> observers;
    private Lock lock;

    public Host(String id) {
        this.id = id;
        lock = new ReentrantLock();
        observers = new ArrayList<>();
        currentWorkStartsAt = Long.MIN_VALUE;
    }

    public Lock getLock() {
        return lock;
    }

    public boolean isAvailable() {
        return lock.tryLock();
    }

    public String getId() {
        return id;
    }

    public boolean isToBeRemoved() {
        return toBeRemoved;
    }

    public void setToBeRemoved(boolean toBeRemoved) {
        this.toBeRemoved = toBeRemoved;
    }

    @Override
    public void addObserver(Observer employee) {
        observers.add(employee);
    }

    @Override
    public void deleteObservers() {
        observers = new ArrayList<>();
    }

    @Override
    public void notifyObservers() {
        List<Observer> observersCopy = observers;
        deleteObservers();
        //Randomly weak up employees
        Collections.shuffle(observersCopy);
        for (int i = 0; i < observersCopy.size(); i++) {
            observersCopy.get(i).weakUp();
        }
    }

    //When one employee is removed during waiting for this host,
    //host should remove the employee from its observer list as well
    public void deleteObserver(Observer employee) {
        Iterator<Observer> iterator = observers.iterator();
        while (iterator.hasNext()) {
            Observer currentEmployee = iterator.next();
            if (currentEmployee.equals(employee)) {
                iterator.remove();
                break;
            }
        }
    }

    public void work() throws InterruptedException {
        try {
            currentWorkStartsAt = System.currentTimeMillis();
            System.out.println(this.id + " start to work");
            Thread.sleep(workingHour);
            System.out.println(this.id + " finished");
        } catch (InterruptedException e) {
            System.out.println(this.id + " was interrupted in the middle");
            throw e;
        } finally {
            long currentWorkEndsAt = System.currentTimeMillis();
            updateWorkingHour(currentWorkStartsAt, currentWorkEndsAt);
        }
    }

    public void weakUpWaitingThreads() {
        notifyObservers();
    }

    private void updateWorkingHour(long start, long end) {
        this.currentWorkStartsAt = Long.MIN_VALUE;
        double hour = TimeTransformer.convertToHour(start, end);
        this.totalWorkingHour += hour;
    }

    //如果说当前处于空闲的状态  那么 应该返回的是上一次工作结束后的时间
    @Override
    public double calculateCurrentWorkingHour(long current) {
        return currentWorkStartsAt < 0 ? this.totalWorkingHour : TimeTransformer.convertToHour(currentWorkStartsAt, current) + totalWorkingHour;
    }
}
