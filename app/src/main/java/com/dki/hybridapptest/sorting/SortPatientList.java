package com.dki.hybridapptest.sorting;

import com.dki.hybridapptest.dto.PatientInfoDTO;

import java.util.Comparator;

public class SortPatientList implements Comparator<PatientInfoDTO> {

    @Override
    public int compare(PatientInfoDTO user1, PatientInfoDTO user2) {
        try {
            if (Integer.parseInt(user1.getNum()) > Integer.parseInt(user2.getNum())) {
                return 1;
            } else if (Integer.parseInt(user1.getNum()) < Integer.parseInt(user2.getNum())) {
                return -1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }
}