package com.zhongbao.platform.model;

import com.zhongbao.platform.util.TimeTransformer;

import java.util.Random;

public class Employee implements Runnable, Observer, Accountable {
    private int nextTargetHostIndex;
    private double totalWorkingHour;
    private long currentWorkStartsAt;
    private String id;

    public Employee(String id) {
        this.id = id;
        nextTargetHostIndex = -1;
        currentWorkStartsAt = Long.MIN_VALUE;
    }

    public String getId() {
        return id;
    }

    //When this employee finishes his work on the host, he should be prepared to ask for the next host
    private void reset() {
        this.nextTargetHostIndex = -1;
    }

    private boolean prepareForTheNextRound() {
        if (nextTargetHostIndex == -1)
            nextTargetHostIndex = calculateIndexOfTargetHost();
        if (isHostPoolEmpty())
            return false;
        //In case the host requested has been removed during waiting
        if (hostRequestedBeforeWaitingNoLongerExists()) {
            reset();
            return false;
        }
        return true;
    }

    private boolean isHostPoolEmpty() {
        return nextTargetHostIndex == -1;
    }

    private boolean hostRequestedBeforeWaitingNoLongerExists() {
        return nextTargetHostIndex != -1 && calculateIndexOfTargetHost() == -1;
    }


    private int calculateIndexOfTargetHost() {
        Random random = new Random();
        int hostAmount = HostPool.hostPool.getHostPoolSize();
        if (hostAmount == 0) {
            System.out.println("No host exists in the pool");
            return -1;
        }
        return random.nextInt(HostPool.hostPool.getHostPoolSize());
    }

    private void updateWorkingHour(long start, long end) {
        this.currentWorkStartsAt = Long.MIN_VALUE;
        double hour = TimeTransformer.convertToHour(start, end);
        this.totalWorkingHour += hour;
    }

    @Override
    public void weakUp() {
        synchronized (this) {
            System.out.println(this.id + " weaks up");
            notify();
        }
    }

    //如果说当前处于空闲的状态  那么 应该返回的是上一次工作结束后的时间
    @Override
    public double calculateCurrentWorkingHour(long current) {
        return currentWorkStartsAt < 0 ? this.totalWorkingHour : TimeTransformer.convertToHour(currentWorkStartsAt, current) + totalWorkingHour;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                if (!prepareForTheNextRound())
                    continue;
                Host host = null;
                //the one requested with index nextTargetHostIndex may be deleted before it is obtained
                try {
                    host = HostPool.hostPool.getHostByIndex(nextTargetHostIndex);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }

                if (host != null && !host.isToBeRemoved()) {
                    System.out.println(this.id + " is asking for " + host.getId());
                    if (host.isAvailable()) {
                        //Host request is successful
                        currentWorkStartsAt = System.currentTimeMillis();
                        System.out.println(this.id + " has got " + host.getId());
                        try {
                            host.work();
                            reset();
                        } catch (InterruptedException e) {
                            //This employee has been removed while working
                            System.out.println(this.id + " stops working immediately");
                            break;
                        } finally {
                            host.getLock().unlock();
                            long currentWorkEndsAt = System.currentTimeMillis();
                            updateWorkingHour(currentWorkStartsAt, currentWorkEndsAt);
                            System.out.println(this.id + " has released " + host.getId());
                            host.weakUpWaitingThreads();
                        }
                    } else {
                        //The host requested has been occupied by others
                        System.out.println(this.id + " did not get  " + host.getId() + " and it is going to wait for " + host.getId());
                        host.addObserver(this);
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            //This employee has been removed while waiting
                            System.out.println(this.id + " stops while waiting");
                            host.deleteObserver(this);
                            break;
                        }
                    }
                } else {
                    nextTargetHostIndex = calculateIndexOfTargetHost();
                }
            }
        }
    }
}
