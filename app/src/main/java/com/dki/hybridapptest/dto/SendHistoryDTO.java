package com.dki.hybridapptest.dto;

public class SendHistoryDTO extends PatientCommonDTO {
    private String time;

    private String bodyStatus;

    public SendHistoryDTO(String name, String patientId) {
        super(name, patientId);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getBodyStatus() {
        return bodyStatus;
    }

    public void setBodyStatus(String bodyStatus) {
        this.bodyStatus = bodyStatus;
    }
}
