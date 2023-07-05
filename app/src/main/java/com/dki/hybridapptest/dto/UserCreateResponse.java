package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserCreateResponse {
    @SerializedName("name")
    private String name;

    @SerializedName("job")
    private String job;

    @SerializedName("id")
    private String id = null;

    @SerializedName("createdAt")
    private String createdAt;

    public UserCreateResponse(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
