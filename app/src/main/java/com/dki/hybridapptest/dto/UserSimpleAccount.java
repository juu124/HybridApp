package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserSimpleAccount {
    @SerializedName("id")
    private String id;

    @SerializedName("pwd")
    private String pwd;

    public UserSimpleAccount(String id, String pwd) {
        this.id = id;
        this.pwd = pwd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
