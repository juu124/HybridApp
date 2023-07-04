package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.InputUserInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.utils.GLog;


public class InputUserDialog extends Dialog {
    private Context mContext;
    private EditText editUserID;
    private EditText editUserEmail;
    private EditText editUserFistName;
    private EditText editUserLastName;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private InputUserInfoListener mInputUserInfoListener;

    public InputUserDialog(@NonNull Context context, InputUserInfoListener inputUserInfoListener) {
        super(context);
        GLog.d();
        mContext = context;
        mInputUserInfoListener = inputUserInfoListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d();
        setContentView(R.layout.dialog_user_add_list_view);
        editUserID = findViewById(R.id.dialog_edit_user_id);
        editUserEmail = findViewById(R.id.dialog_edit_user_email);
        editUserFistName = findViewById(R.id.dialog_edit_user_first_name);
        editUserLastName = findViewById(R.id.dialog_edit_user_last_name);

        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Integer.parseInt(editUserID.getText().toString());
                } catch (Exception e) {
                    GLog.e("Exception == " + e.toString());
                    return;
                }
                mInputUserInfoListener.onInputPositiveClick(editUserID.getText().toString(),
                        editUserEmail.getText().toString(),
                        editUserFistName.getText().toString(),
                        editUserLastName.getText().toString());
                dialogYesBtn.setEnabled(true);

                dismiss();
                GLog.d("입력한 값은 \n" + "id == " + editUserID.getText().toString() + "\n" +
                        "email == " + editUserEmail.getText().toString() + "\n" +
                        "First Name == " + editUserFistName.getText().toString() + "\n" +
                        "Last Name == " + editUserLastName.getText().toString());
            }
        });

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputUserInfoListener.onInputNegativeClick();
                dismiss();
            }
        });
    }
}
