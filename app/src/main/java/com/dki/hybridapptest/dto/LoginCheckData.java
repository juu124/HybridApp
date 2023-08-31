package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class LoginCheckData {
    @SerializedName("isLoginCheck")
    private boolean isLoginCheck;

    public LoginCheckData(boolean isLoginCheck) {
        this.isLoginCheck = isLoginCheck;
    }

    public boolean getIsLoginCheck() {
        return isLoginCheck;
    }

    public void setIsLoginCheck(boolean isLoginCheck) {
        this.isLoginCheck = isLoginCheck;
    }
}
