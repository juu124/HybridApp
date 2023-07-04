package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserCreate extends UserCommon {
    @SerializedName("name")
    private String name;

    @SerializedName("job")
    private String job;

//    @SerializedName("id")
//    private String id = null;
//
//    @SerializedName("createdAt")
//    private String createdAt;

    public UserCreate(String mName, String mJob) {
        name = mName;
        job = mJob;
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

//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(String createdAt) {
//        this.createdAt = createdAt;
//    }
}