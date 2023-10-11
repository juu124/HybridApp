package com.dki.hybridapptest.dto;

import java.io.Serializable;

public class SendLogDTO implements Serializable {
    private String time;

    private String name;

    private String id;

    private String sendLog;

//    public SendLogDTO(String time, String name, String patientId, String sendLog) {
//        super(name, patientId);
//        this.time = time;
//        this.sendLog = sendLog;
//    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSendLog() {
        return sendLog;
    }

    public void setSendLog(String sendLog) {
        this.sendLog = sendLog;
    }
}
