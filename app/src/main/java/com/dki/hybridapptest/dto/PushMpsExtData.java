package com.dki.hybridapptest.dto;

import com.google.gson.annotations.SerializedName;

public class PushMpsExtData {
    @SerializedName("appid")
    private String appId;

    @SerializedName("seqno")
    private String seqNo;

    @SerializedName("ext")
    private PushMpsImgUrl ext;

    @SerializedName("sender")
    private String sender;

    @SerializedName("senddate")
    private String sendDate;

    @SerializedName("db_in")
    private String dbIn;

    @SerializedName("pushkey")
    private String pushKey;

    @SerializedName("sms")
    private String sms;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public PushMpsImgUrl getExt() {
        return ext;
    }

    public void setExt(PushMpsImgUrl ext) {
        this.ext = ext;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getDbIn() {
        return dbIn;
    }

    public void setDbIn(String dbIn) {
        this.dbIn = dbIn;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }
}
