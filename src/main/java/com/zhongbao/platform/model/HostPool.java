package com.zhongbao.platform.model;

import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;

public class HostPool {
    private boolean isHouseKeeping = false;
    private int initialHostNumber = 10;
    private Vector<Host> hosts = null;
    private int poolSize = initialHostNumber;

    public static HostPool hostPool = new HostPool();

    private HostPool() {

    }

    public synchronized int getHostPoolSize() {
        return poolSize;
    }

    public void createHostPool() {
        if (hosts == null) {
            hosts = new Vector<>();

            for (int i = 0; i < initialHostNumber; i++) {
                Host host = new Host("host" + UUID.randomUUID().toString());
                hosts.add(host);
            }
        }
    }

    public Host getHostByIndex(int hostIndex) {
        if (hosts != null && !isHouseKeeping) {
            return hosts.get(hostIndex);
        }
        return null;
    }

    public Host getHostById(String id) {
        if (hosts != null && !isHouseKeeping) {
            Optional<Host> currentHost = hosts.stream().filter(aHost -> aHost.getId().equals(id)).findFirst();
            return currentHost.isPresent() ? currentHost.get() : null;
        }
        return null;
    }


    public synchronized void addHost() {
        Host host = new Host("host" + UUID.randomUUID().toString());
        poolSize++;
        hosts.add(host);
        System.out.println(hostPoolSizeMessage());
    }

    public synchronized void removeHost(String id) {
        Optional<Host> host = hosts.stream().filter(aHost -> aHost.getId().equals(id)).findFirst();
        if (host.isPresent()) {
            Host currentHost = host.get();
            currentHost.setToBeRemoved(true);
            while (!currentHost.isAvailable()) {
            }
            try {
                System.out.println("removed");
                hosts.remove(currentHost);
            } finally {
                System.out.println(id + " has been removed");
                unlockHostAndWeakUpWaitingEmployees(currentHost);
            }
        }
        System.out.println(hostPoolSizeMessage());
    }

    private void unlockHostAndWeakUpWaitingEmployees(Host currentHost) {
        currentHost.getLock().unlock();
        currentHost.weakUpWaitingThreads();
    }

    public synchronized void removeHost() {
        while (true) {
            Iterator<Host> hostIterator = hosts.iterator();
            while (hostIterator.hasNext()) {
                Host host = hostIterator.next();
                if (host.isAvailable()) {
                    try {
                        hostIterator.remove();
                        poolSize--;
                    } finally {
                        System.out.println(hostRemoveMessage(host));
                        host.getLock().unlock();
                        resetRemoveFlagOnHosts();
                        host.weakUpWaitingThreads();
                        System.out.println(hostPoolSizeMessage());
                        return;
                    }
                } else {
                    host.setToBeRemoved(true);
                }
            }
        }
    }

    private void resetRemoveFlagOnHosts() {
        for (Host host1 : hosts) {
            host1.setToBeRemoved(false);
        }
    }

    // if there are more hosts than employees, then housekeeping works
    public synchronized void hostPoolHouseKeeping() {
        if (hosts != null) {
            isHouseKeeping = true;
            Iterator<Host> allHosts = hosts.iterator();

            if (isHouseKeepingNecessary()) {
                while (allHosts.hasNext()) {
                    Host currentHost = allHosts.next();

                    if (currentHost.isAvailable()) {
                        try {
                            hosts.remove(currentHost);
                            poolSize--;
                        } finally {
                            System.out.println(hostRemoveMessage(currentHost));
                            unlockHostAndWeakUpWaitingEmployees(currentHost);
                        }
                    }
                }
            }
            isHouseKeeping = false;
        }
    }

    // if there are more hosts than employees, then housekeeping works
    private boolean isHouseKeepingNecessary() {
        return poolSize > EmployeePool.employeePool.getPoolSize();
    }

    private String hostRemoveMessage(Host currentHost) {
        return currentHost.getId() + " has been removed";
    }

    private String hostPoolSizeMessage() {
        return "New host pool size is " + getHostPoolSize();
    }
}
