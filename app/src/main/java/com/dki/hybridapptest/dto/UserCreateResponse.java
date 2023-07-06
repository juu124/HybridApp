package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserCreateResponse extends UserCreate {

    @SerializedName("id")
    private String id = null;

    @SerializedName("createdAt")
    private String createdAt;

    public UserCreateResponse(String mName, String mJob, String id, String createdAt) {
        super(mName, mJob);
        this.id = id;
        this.createdAt = createdAt;
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
