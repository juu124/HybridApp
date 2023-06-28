package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class UserResponseSupport extends UserCommon {
    @SerializedName("url")
    private String url;

    @SerializedName("text")
    private String text;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
