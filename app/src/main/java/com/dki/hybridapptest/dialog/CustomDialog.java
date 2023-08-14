package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;

public class CustomDialog extends Dialog {
    private Context mContext;
    private Button btnCancle;
    private Button btnConfirm;
    private TextView tvDialogTitle;
    private TextView tvDialogContent;
    private CustomDialogClickListener mCustomDialogClickListener;
    private String title;
    private String message;
    private boolean isAutoDissmiss;
    private String buttonType;


    public CustomDialog(@NonNull Context context, CustomDialogClickListener customDialogClickListener, String title, String message, String buttonType, boolean isAutoDismiss) {
        super(context);
        mContext = context;
        mCustomDialogClickListener = customDialogClickListener;
        this.title = title;
        this.message = message;
        this.buttonType = buttonType;
        this.isAutoDissmiss = isAutoDismiss;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d();
        setContentView(R.layout.dialog_yes_no);
        btnCancle = findViewById(R.id.dialog_user_info_no_button);
        btnConfirm = findViewById(R.id.dialog_user_info_yes_button);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        tvDialogContent = findViewById(R.id.tv_dialog_content);

        tvDialogTitle.setText(title);
        tvDialogContent.setText(message);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomDialogClickListener != null) {
                    mCustomDialogClickListener.onPositiveClick("");
                    if (isAutoDissmiss) {
                        dismiss();
                    }
                }
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCustomDialogClickListener != null) {
                    mCustomDialogClickListener.onNegativeClick();
                    if (isAutoDissmiss) {
                        dismiss();
                    }
                }
            }
        });

        if (buttonType.equals(Constant.ONE_BUTTON)) {
            btnCancle.setVisibility(View.GONE);
            btnConfirm.setText("확인");
        } else {
            btnCancle.setVisibility(View.VISIBLE);
            btnConfirm.setText("예");
            btnCancle.setText("아니오");
        }
    }

    // 버튼 1개
    public void setOneButtonText(String text) {
        btnConfirm.setText(text);
    }

    // 버튼 2개
    public void setTwoButtonText(String negativeText, String positiveText) {
        btnCancle.setText(negativeText);
        btnConfirm.setText(positiveText);
    }
}