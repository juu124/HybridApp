package com.dki.hybridapptest.DTO;

import com.google.gson.annotations.SerializedName;

public class DTORetrofitSupport {
    @SerializedName("url")
    String url;

    @SerializedName("text")
    String text;

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
