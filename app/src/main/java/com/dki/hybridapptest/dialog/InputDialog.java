package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.InputDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.GLog;

public class InputDialog extends Dialog {
    private Context mContext;
    private EditText dialogEditText;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private InputDialogClickListener mCustomDialogClickListener;

    public InputDialog(@NonNull Context context, InputDialogClickListener customDialogClickListener) {
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

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialogClickListener.onInputPositiveClick(dialogEditText.getText().toString());
                dismiss();
            }
        });

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomDialogClickListener.onInputNegativeClick();
                dismiss();
            }
        });
    }
}