package com.example.test.domain;

import com.google.gson.annotations.SerializedName;

public final class ApiResponseRest {
    @SerializedName("user")
    public UserRest user;
}