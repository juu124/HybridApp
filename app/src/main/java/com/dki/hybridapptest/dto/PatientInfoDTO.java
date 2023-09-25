package com.dki.hybridapptest.dto;

public class PatientInfoDTO extends PatientCommonDTO {
    private String num;

    private String gender;

    private String bornYear;

    public PatientInfoDTO(String name, String patientId) {
        super(name, patientId);
    }

    public PatientInfoDTO(String gender, String name, String patientId, String bornYear) {
        super(name, patientId);
        this.gender = gender;
        this.bornYear = bornYear;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBornYear() {
        return bornYear;
    }

    public void setBornYear(String bornYear) {
        this.bornYear = bornYear;
    }
}
