package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
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
    private Dialog dialog;
    private TextView userInfo;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private String text = null;

    public UserDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public void userInfoSetting(String id, String email, String firstName, String lastName) {
        text = "Id : " + id + "\n"
                + "Email : " + email + "\n"
                + "Name : " + firstName + " " + lastName;
        GLog.d("text 잘 나왔나" + text);
    }

    public void showDialog(int position) {
        GLog.d("showDialog");
        dialog = new Dialog(mContext);

        userInfo = dialog.findViewById(R.id.user_info);
        dialogNoBtn = dialog.findViewById(R.id.noButton);
        dialogYesBtn = dialog.findViewById(R.id.yesButton);

        RetrofitApiManager.getInstance().requestOneUserInfo((position + 1) + "", new RetrofitInterface() {
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

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
