package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.ui.activity.WebViewSizeChangeActivity;
import com.dki.hybridapptest.utils.GLog;
import com.dki.hybridapptest.utils.SharedPreferencesAPI;

public class InputDialog extends Dialog {
    private Context mContext;
    private EditText dialogEditText;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private CustomDialogClickListener mCustomDialogClickListener;
    private RadioButton radioBtnFullMode;
    private RadioButton radioBtnHalfMode;
    private RadioGroup radioGroup;

    private String url;
    private boolean isFullMode;
    private Intent mIntent;

    public InputDialog(@NonNull Context context, CustomDialogClickListener customDialogClickListener) {
        super(context);
        mContext = context;
        mCustomDialogClickListener = customDialogClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d();
        setContentView(R.layout.dialog_edit_view);
        dialogEditText = findViewById(R.id.dialog_edit);
        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);
        radioGroup = findViewById(R.id.radio_btn_layout);
        radioBtnFullMode = findViewById(R.id.radio_btn_full_mode);
        radioBtnHalfMode = findViewById(R.id.radio_btn_half_mode);

        radioGroup.check(radioBtnFullMode.getId());  // 체크박스에서 radioBtnFullMode가 디폴트로 체크된 상태
        dialogEditText.setText(SharedPreferencesAPI.getInstance(mContext).getUrl());

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = dialogEditText.getText().toString();
                GLog.d("url == " + url);
                if (!TextUtils.isEmpty(url)) {
                    SharedPreferencesAPI.getInstance(mContext).setUrl(url);
                    moveWebView(url);
                    mCustomDialogClickListener.onPositiveClick(url);
                    dismiss();
                } else {
                    Toast.makeText(mContext, "url을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialogClickListener.onNegativeClick();
                dismiss();
            }
        });
    }

    // 크기 버튼 체크
    public void moveWebView(String url) {
        if (radioBtnFullMode.isChecked()) { // 풀 모드
            isFullMode = true;
        } else if (radioBtnHalfMode.isChecked()) { // 하프 모드
            isFullMode = false;
        } else {
            Toast.makeText(mContext, "화면 모드를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
        mIntent = new Intent(mContext, WebViewSizeChangeActivity.class);
        mIntent.putExtra("url", url);
        mIntent.putExtra("isFullMode", isFullMode);
        mContext.startActivity(mIntent);
    }
}