package com.dki.hybridapptest.sorting;

import com.dki.hybridapptest.dto.UserResponse;

import java.util.Comparator;

public class SortArrayList implements Comparator<UserResponse> {

    @Override
    public int compare(UserResponse user1, UserResponse user2) {
        if (Integer.parseInt(user1.getId()) > Integer.parseInt(user2.getId())) {
            return 1;
        } else if (Integer.parseInt(user1.getId()) < Integer.parseInt(user2.getId())) {
            return -1;
        } else {
            return 0;
        }
    }
}