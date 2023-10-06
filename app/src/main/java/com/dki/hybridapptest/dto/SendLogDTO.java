package com.dki.hybridapptest.dto;

import java.io.Serializable;

public class SendLogDTO extends PatientCommonDTO implements Serializable {
    private String time;

    private String sendLog;

    public SendLogDTO(String name, String patientId) {
        super(name, patientId);
    }

    public SendLogDTO(String name, String patientId, String sendLog) {
        super(name, patientId);
        this.sendLog = sendLog;
    }

    public SendLogDTO(String time, String name, String patientId, String sendLog) {
        super(name, patientId);
        this.time = time;
        this.sendLog = sendLog;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSendLog() {
        return sendLog;
    }

    public void setSendLog(String sendLog) {
        this.sendLog = sendLog;
    }
}
