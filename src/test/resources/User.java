package com.example.test.entity;

import java.util.List;
import org.parceler.Parcel;

@Parcel
public final class User {
    String name;

    long age;

    boolean kawaii;

    double height;

    List<Subscriber> subscribers;

    Status status;

    List<Comment> comments;

    Image image;

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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}