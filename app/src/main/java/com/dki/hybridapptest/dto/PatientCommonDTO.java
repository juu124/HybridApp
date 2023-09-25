package com.dki.hybridapptest.dto;

public class PatientCommonDTO {
    private String name;

    private String patientId;

    public PatientCommonDTO(String name, String patientId) {
        this.name = name;
        this.patientId = patientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
