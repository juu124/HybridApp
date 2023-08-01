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
import com.dki.hybridapptest.utils.GLog;

public class CustomYesNoDialog extends Dialog {
    private Context mContext;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private TextView tvDialogTitle;
    private TextView tvDialogContent;
    private CustomDialogClickListener mCustomDialogClickListener;
    private String title;
    private String message;
    private String positiveBtn;
    private String negativeBtn;
    private boolean isAutoDissmiss;

    public CustomYesNoDialog(@NonNull Context context, CustomDialogClickListener customDialogClickListener, String title, String message, boolean isAutoDismiss, String positiveBtn, String negativeBtn) {
        super(context);
        mContext = context;
        mCustomDialogClickListener = customDialogClickListener;
        this.title = title;
        this.message = message;
        this.isAutoDissmiss = isAutoDismiss;
        this.positiveBtn = positiveBtn;
        this.negativeBtn = negativeBtn;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d();
        setContentView(R.layout.dialog_yes_no);
        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);
        tvDialogTitle = findViewById(R.id.tv_dialog_title);
        tvDialogContent = findViewById(R.id.tv_dialog_content);

        dialogYesBtn.setText(positiveBtn);
        dialogNoBtn.setText(negativeBtn);

        tvDialogTitle.setText(title);
        tvDialogContent.setText(message);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
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

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
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
    }
}