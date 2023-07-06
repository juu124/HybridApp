package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.InputUserInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.UserResponse;
import com.dki.hybridapptest.utils.GLog;

import java.util.regex.Pattern;


public class InputUserDialog extends Dialog {
    private Context mContext;
    private EditText editUserID;
    private EditText editUserEmail;
    private EditText editUserFirstName;
    private EditText editUserLastName;
    private Button dialogNoBtn;
    private Button dialogYesBtn;
    private InputUserInfoListener mInputUserInfoListener;
    private Pattern pattern = Patterns.EMAIL_ADDRESS;
    private UserResponse mUser;

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

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
        editUserFirstName = findViewById(R.id.dialog_edit_user_first_name);
        editUserLastName = findViewById(R.id.dialog_edit_user_last_name);

        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = editUserID.getText().toString();
                String email = editUserEmail.getText().toString();
                String firstName = editUserFirstName.getText().toString();
                String lastName = editUserLastName.getText().toString();
                mUser = new UserResponse(id, email, firstName, lastName, "");

                if (TextUtils.isEmpty(id)) {
                    editUserID.requestFocus();
                    showKeyboard(editUserID);
                    Toast.makeText(mContext, "Id를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        Integer.parseInt(id);
                    } catch (Exception e) {
                        GLog.e("Exception ID == " + e);
                        Toast.makeText(mContext, "Id는 숫자만 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (TextUtils.isEmpty(email)) {
                    editUserEmail.requestFocus();
                    showKeyboard(editUserEmail);
                    Toast.makeText(mContext, "email를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!pattern.matcher(email).matches()) {
                        showKeyboard(editUserEmail);
                        Toast.makeText(mContext, "email 형식으로 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (TextUtils.isEmpty(firstName)) {
                    editUserFirstName.requestFocus();
                    showKeyboard(editUserFirstName);
                    Toast.makeText(mContext, "FirstName를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(lastName)) {
                    editUserLastName.requestFocus();
                    showKeyboard(editUserLastName);
                    Toast.makeText(mContext, "LastName를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mInputUserInfoListener.onInputPositiveClick(mUser);

                GLog.d("입력한 값은 \n" + "id == " + id + "\n" +
                        "email == " + email + "\n" +
                        "First Name == " + firstName + "\n" +
                        "Last Name == " + lastName);
                dismiss();
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