package com.dki.hybridapptest.dto;

public class RecodePatientDTO {
    private boolean isChecked;

    private String time;

    private String type;

    private String recodePatient;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRecodePatient() {
        return recodePatient;
    }

    public void setRecodePatient(String recodePatient) {
        this.recodePatient = recodePatient;
    }

}
