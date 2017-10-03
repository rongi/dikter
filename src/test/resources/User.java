package com.example.test.entity;

import java.util.List;

public final class User {
    private String name;

    private long age;

    private boolean kawaii;

    private double height;

    private List<Subscriber> subscribers;

    private Status status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAge() {
        return age;
    }

    public void setAge(long age) {
        this.age = age;
    }

    public boolean getKawaii() {
        return kawaii;
    }

    public void setKawaii(boolean kawaii) {
        this.kawaii = kawaii;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(List<Subscriber> subscribers) {
        this.subscribers = subscribers;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}