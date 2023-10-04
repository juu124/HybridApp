package com.dki.hybridapptest.dto;

public class RecodePatientDTO extends PatientCommonDTO {
    private String time;

    private String type;

    private String bodyStatus;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RecodePatientDTO(String name, String patientId) {
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
