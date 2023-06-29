package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import retrofit2.Response;

public class UserDialog extends Dialog {
    private Context mContext;
    private TextView userInfo;
    private Button dialogYesBtn;
    private String text = null;
    private int mPosition;

    public UserDialog(@NonNull Context context, int position) {
        super(context);
        mContext = context;
        mPosition = position;
    }

    public void userInfoSetting(String id, String email, String firstName, String lastName) {
        text = "Id : " + id + "\n"
                + "Email : " + email + "\n"
                + "Name : " + firstName + " " + lastName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view);
        userInfo = findViewById(R.id.user_info);
        dialogYesBtn = findViewById(R.id.yesButton);

        RetrofitApiManager.getInstance().requestOneUserInfo((mPosition + 1) + "", new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
                GLog.d("onResponse");
                UserDataSupport userResponse = (UserDataSupport) response.body();
                userInfoSetting(userResponse.getDtoUser().getId(), userResponse.getDtoUser().getEmail(), userResponse.getDtoUser().getFirstName(), userResponse.getDtoUser().getLastName());
                userInfo.setText(text);
            }

            @Override
            public void onFailure(Throwable t) {
                GLog.d("오류 메세지 == " + t.toString());
            }
        });

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
