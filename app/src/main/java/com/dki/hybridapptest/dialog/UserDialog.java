package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.retrofit.RetrofitApiManager;
import com.dki.hybridapptest.retrofit.RetrofitInterface;
import com.dki.hybridapptest.utils.GLog;

import retrofit2.Response;

public class UserDialog extends Dialog {
    private Context mContext;
    private TableLayout itemUserLayout;
    private Button dialogYesBtn;
    private String text = null;
    private String mPosition;
    private TextView item_user_id_tv;
    private TextView item_user_email_tv;
    private TextView item_user_first_name_tv;
    private TextView item_user_last_name_tv;
    private UserResponse mUser;
    private boolean mType;

    private String userPosition;
    private String userEmail;
    private String userFirstName;
    private String userLastName;

    // 프로그래스 바
    private ProgressBar mProgressBar;

    public UserDialog(@NonNull Context context, String position, boolean type) {
        super(context);
        mContext = context;
        mPosition = position;
        mType = type;
    }

    public UserDialog(@NonNull Context context, String position, String email, String firstName, String lastName) {
        super(context);
        mContext = context;
        userPosition = position;
        userEmail = email;
        userFirstName = firstName;
        userLastName = lastName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_view);
        item_user_id_tv = findViewById(R.id.item_user_id_tv);
        item_user_email_tv = findViewById(R.id.item_user_email_tv);
        item_user_first_name_tv = findViewById(R.id.item_user_first_name_tv);
        item_user_last_name_tv = findViewById(R.id.item_user_last_name_tv);

        itemUserLayout = findViewById(R.id.item_user_layout);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);
        mProgressBar = findViewById(R.id.indeterminate_progressbar);

        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.VISIBLE);
        dialogYesBtn.setEnabled(false);

        if (mType) {
            GLog.d("서버 데이터가 맞습니다 =====");
            RetrofitApiManager.getInstance().requestOneUserInfo(Integer.parseInt(mPosition) + "", new RetrofitInterface() {
                @Override
                public void onResponse(Response response) {
                    if (response.isSuccessful() && response.body() != null) {
                        GLog.d("onResponse");
                        UserDataSupport userResponse = (UserDataSupport) response.body();
                        mUser = userResponse.getDtoUser();
                        if (userResponse.getDtoUser() != null) {
                            mProgressBar.setVisibility(View.GONE);
                            item_user_id_tv.setText(mUser.getId());
                            item_user_email_tv.setText(mUser.getEmail());
                            item_user_first_name_tv.setText(mUser.getFirstName());
                            item_user_last_name_tv.setText(mUser.getLastName());
                            GLog.d("" + item_user_id_tv.getText());
                            dialogYesBtn.setEnabled(true);
                            GLog.d("text 있음");
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
        } else {
            GLog.d("서버 데이터가 아닙니다 =====");
            mProgressBar.setVisibility(View.GONE);
            item_user_id_tv.setText(userPosition);
            item_user_email_tv.setText(userEmail);
            item_user_first_name_tv.setText(userFirstName);
            item_user_last_name_tv.setText(userLastName);
            dialogYesBtn.setEnabled(true);
            GLog.d("text 있음");
        }

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
