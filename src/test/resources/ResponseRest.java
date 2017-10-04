package com.example.test.domain;

import com.google.gson.annotations.SerializedName;

public final class ResponseRest {
    @SerializedName("user")
    public UserRest user;
}