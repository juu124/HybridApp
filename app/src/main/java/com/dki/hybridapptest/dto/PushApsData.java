package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class PushApsData {
    @SerializedName("badge")
    private String badge;

    @SerializedName("sound")
    private String sound;

    @SerializedName("alert")
    private PushApsAlertData alert;

    @SerializedName("protocol")
    private String protocol;

    @SerializedName("sub_title")
    private String subTitle;

    public String getBadge() {
        return badge;
    }

    public void setBadge(String badge) {
        this.badge = badge;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public PushApsAlertData getAlert() {
        return alert;
    }

    public void setAlert(PushApsAlertData alert) {
        this.alert = alert;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }
}
