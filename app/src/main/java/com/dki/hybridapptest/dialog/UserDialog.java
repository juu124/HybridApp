package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.RemoveUserListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import retrofit2.Response;

public class UserDialog extends Dialog {
    private Context mContext;
    private Button dialogYesBtn;
    private Button dialogRemoveBtn;
    private TextView item_user_id_tv;
    private TextView item_user_email_tv;
    private TextView item_user_first_name_tv;
    private TextView item_user_last_name_tv;
    private UserResponse mUser;
    private RemoveUserListener mRemoveUserListener;

    // 프로그래스 바
    private RelativeLayout mProgressBar;

    public UserDialog(@NonNull Context context, UserResponse userResponse, RemoveUserListener removeUserListener) {
        super(context);
        mContext = context;
        mUser = userResponse;
        mRemoveUserListener = removeUserListener;
        GLog.d("UserDialog ===== " + GLog.toJson(mUser));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view);
        item_user_id_tv = findViewById(R.id.item_user_id_tv);
        item_user_email_tv = findViewById(R.id.item_user_email_tv);
        item_user_first_name_tv = findViewById(R.id.item_user_first_name_tv);
        item_user_last_name_tv = findViewById(R.id.item_user_last_name_tv);

        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);
        dialogRemoveBtn = findViewById(R.id.dialog_user_info_remove_button);
        mProgressBar = findViewById(R.id.dialog_user_info_progressbar);

        mProgressBar.setVisibility(View.VISIBLE);
        dialogYesBtn.setEnabled(false);
        dialogRemoveBtn.setEnabled(false);

        if (mUser.isAdd()) {
            GLog.d("서버 데이터가 아닙니다 =====");
            mProgressBar.setVisibility(View.GONE);
            item_user_id_tv.setText(mUser.getId());
            item_user_email_tv.setText(mUser.getEmail());
            item_user_first_name_tv.setText(mUser.getFirstName());
            item_user_last_name_tv.setText(mUser.getLastName());
            dialogYesBtn.setEnabled(true);
            dialogRemoveBtn.setEnabled(true);
        } else {
            GLog.d("서버 데이터가 맞습니다 =====");
            RetrofitApiManager.getInstance().requestOneUserInfo(mUser.getId(), new RetrofitInterface() {
                @Override
                public void onResponse(Response response) {
                    GLog.d("onResponse");
                    if (response.isSuccessful() && response.body() != null) {
                        UserDataSupport userResponse = (UserDataSupport) response.body();
                        mUser = userResponse.getDtoUser();
                        if (mUser != null) {
                            mProgressBar.setVisibility(View.GONE);
                            item_user_id_tv.setText(mUser.getId());
                            item_user_email_tv.setText(mUser.getEmail());
                            item_user_first_name_tv.setText(mUser.getFirstName());
                            item_user_last_name_tv.setText(mUser.getLastName());
                            GLog.d("클릭한 서버 데이터 출력 == " + item_user_id_tv.getText());
                            dialogYesBtn.setEnabled(true);
                            dialogRemoveBtn.setEnabled(true);
                        } else {
                            GLog.d("text 없음" + response.errorBody());
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    GLog.d("오류 메세지 == " + t.toString());
                }
            });
        }

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        dialogRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRemoveUserListener.onRemoveClick();
                Toast.makeText(mContext, "삭제", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
    }
}