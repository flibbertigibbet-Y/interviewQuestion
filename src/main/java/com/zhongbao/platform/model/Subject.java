package com.zhongbao.platform.model;

public interface Subject {
    void addObserver(Observer obj);

    void deleteObservers();

    void notifyObservers();
}
