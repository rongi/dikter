package com.example.test.domain;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class UserRest {
    @SerializedName("name")
    public String name;

    @SerializedName("age")
    public long age;

    @SerializedName("kawaii")
    public boolean kawaii;

    @SerializedName("height")
    public double height;

    @SerializedName("subscribers")
    public List<SubscriberRest> subscribers;

    @SerializedName("status")
    public StatusRest status;
}