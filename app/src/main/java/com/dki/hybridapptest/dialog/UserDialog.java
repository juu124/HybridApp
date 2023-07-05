package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
    private int mPosition;
    private TextView item_user_id_tv;
    private TextView item_user_email_tv;
    private TextView item_user_first_name_tv;
    private TextView item_user_last_name_tv;
    private UserResponse mUser;
    private RemoveUserListener mRemoveUserListener;
    private String mId;
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private boolean mIsAdd;

    // 프로그래스 바
    private ProgressBar mProgressBar;

    public UserDialog(@NonNull Context context, boolean isAdd, String id, String email, String firstName, String lastName, RemoveUserListener removeUserListener) {
        super(context);
        mContext = context;
        mIsAdd = isAdd;
        mId = id;
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mRemoveUserListener = removeUserListener;
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
        mProgressBar = findViewById(R.id.indeterminate_progressbar);

        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        dialogYesBtn.setEnabled(false);
        dialogRemoveBtn.setEnabled(false);

        if (mIsAdd) {
            GLog.d("서버 데이터가 아닙니다 =====");
            mProgressBar.setVisibility(View.GONE);
            item_user_id_tv.setText(mId);
            item_user_email_tv.setText(mEmail);
            item_user_first_name_tv.setText(mFirstName);
            item_user_last_name_tv.setText(mLastName);
            dialogYesBtn.setEnabled(true);
            dialogRemoveBtn.setEnabled(true);
            GLog.d("text 있음");
        } else {
            GLog.d("서버 데이터가 맞습니다 =====");
            RetrofitApiManager.getInstance().requestOneUserInfo(mId, new RetrofitInterface() {
                @Override
                public void onResponse(Response response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GLog.d("onResponse");
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