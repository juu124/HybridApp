package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.R;
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

    // 프로그래스 바
    private ProgressBar mProgressBar;

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
        mProgressBar = findViewById(R.id.dialog_progressbar);

        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        dialogYesBtn.setEnabled(false);
        RetrofitApiManager.getInstance().requestOneUserInfo((mPosition + 1) + "", new RetrofitInterface() {
            @Override
            public void onResponse(Response response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    GLog.d("onResponse");
//                    UserDataSupport userResponse = (UserDataSupport) response.body();
//                    UserResponse user = userResponse.getDtoUser();
//                    userInfoSetting(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName());
//                    if (userResponse.getDtoUser() != null) {
//                        mProgressBar.setVisibility(View.GONE);
//                        userInfo.setText(text);
//                        dialogYesBtn.setEnabled(true);
//                        GLog.d("text 있음");
//                    } else {
////                        progressBarText.setText("오류 발생" + response.errorBody().toString());
//                        GLog.d("text 없음");
//                    }
//                }
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
