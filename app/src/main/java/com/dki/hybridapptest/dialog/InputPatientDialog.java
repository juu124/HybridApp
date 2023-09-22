package com.dki.hybridapptest.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dki.hybridapptest.Interface.InputPatientInfoListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dto.PatientInfoDTO;
import com.dki.hybridapptest.utils.GLog;

public class InputPatientDialog extends Dialog {
    private Context mContext;
    private EditText patientGender;
    private EditText patientName;
    private EditText patientBornYear;

    private Button dialogNoBtn;
    private Button dialogYesBtn;

    // 성별 라디오 버튼
    private RadioButton manRadioBtn;
    private RadioButton womanRadioBtn;
    private RadioGroup radioGroup;
    private boolean isMan;
    private String gender;

    private int id = 100000;

    private InputPatientInfoListener mInputPatientInfoListener;
    private PatientInfoDTO patientInfoDTO;

    public void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    public InputPatientDialog(@NonNull Context context, InputPatientInfoListener inputPatientInfoListener) {
        super(context);
        GLog.d();
        mContext = context;
        mInputPatientInfoListener = inputPatientInfoListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GLog.d();
        setContentView(R.layout.dialog_patient_add_list_view);
        patientName = findViewById(R.id.edit_patient_name);
        patientBornYear = findViewById(R.id.edit_patient_born_year);

        manRadioBtn = findViewById(R.id.radioButton);
        womanRadioBtn = findViewById(R.id.radioButton2);
        radioGroup = findViewById(R.id.radioGroup);

        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);

        radioGroup.check(manRadioBtn.getId());

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGender();
                String name = patientName.getText().toString();
                String bornYear = patientBornYear.getText().toString();
                int autoId = 0;

                patientInfoDTO = new PatientInfoDTO(gender, name, autoId, bornYear);

                if (TextUtils.isEmpty(name)) {
                    patientName.requestFocus();
                    showKeyboard(patientName);
                    Toast.makeText(mContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(bornYear)) {
                    patientBornYear.requestFocus();
                    showKeyboard(patientBornYear);
                    Toast.makeText(mContext, "생년을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mInputPatientInfoListener.onInputPositiveClick(patientInfoDTO);
                dismiss();
            }
        });

        dialogNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputPatientInfoListener.onInputNegativeClick();
                dismiss();
            }
        });
    }

    // 성별 여부 결정
    public void chooseGender() {
        if (manRadioBtn.isChecked()) { // 풀 모드
            isMan = true;
        } else if (womanRadioBtn.isChecked()) { // 하프 모드
            isMan = false;
        } else {
            Toast.makeText(mContext, "성별을 선택해주세요.", Toast.LENGTH_SHORT).show();
        }

        if (isMan) {
            gender = "남";
        } else {
            gender = "여";
        }
    }
}