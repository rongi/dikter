package com.example.test.entity;

import org.parceler.Parcel;

@Parcel
public final class ApiResponse {
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}