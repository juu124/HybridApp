package com.dki.hybridapptest.dto;

public class UserAccount {
    private String id;
    private String pwd;
    private boolean isAutoLogin;

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

    public boolean isAutoLogin() {
        return isAutoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.isAutoLogin = autoLogin;
    }
}