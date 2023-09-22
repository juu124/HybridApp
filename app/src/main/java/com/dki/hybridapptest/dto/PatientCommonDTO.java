package com.dki.hybridapptest.dto;

public class PatientCommonDTO {
    private String name;

    private int patientId;

    public PatientCommonDTO(String name, int patientId) {
        this.name = name;
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
}
