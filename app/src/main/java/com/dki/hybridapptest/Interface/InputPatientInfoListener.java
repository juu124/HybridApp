package com.dki.hybridapptest.Interface;

import com.dki.hybridapptest.dto.PatientInfoDTO;

public interface InputPatientInfoListener {
    public void onInputPositiveClick(PatientInfoDTO patientInfoDTO);

    public void onInputNegativeClick();
}
