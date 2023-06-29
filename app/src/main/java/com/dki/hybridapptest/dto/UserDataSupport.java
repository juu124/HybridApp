package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserDataSupport {
    @SerializedName("data")
    private UserResponse dtoUser;

    @SerializedName("support")
    private UserResponseSupport dtoSupport;

    public UserResponse getDtoUser() {
        return dtoUser;
    }

    public void setDtoUser(UserResponse dtoUser) {
        this.dtoUser = dtoUser;
    }

    public UserResponseSupport getDtoSupport() {
        return dtoSupport;
    }

    public void setDtoSupport(UserResponseSupport dtoSupport) {
        this.dtoSupport = dtoSupport;
    }
}
