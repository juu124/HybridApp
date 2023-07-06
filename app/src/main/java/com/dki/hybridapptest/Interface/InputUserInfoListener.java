package com.dki.hybridapptest.Interface;

import com.dki.hybridapptest.dto.UserResponse;

public interface InputUserInfoListener {
    public void onInputPositiveClick(UserResponse userResponse);

    public void onInputNegativeClick();
}
