package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("data")
    private LoginCheckData data;

    @SerializedName("resultCode")
    private String resultCode;

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public LoginCheckData getData() {
        return data;
    }

    public void setData(LoginCheckData data) {
        this.data = data;
    }
}
