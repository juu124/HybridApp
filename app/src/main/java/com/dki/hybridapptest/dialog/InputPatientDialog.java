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

import java.util.Random;

public class InputPatientDialog extends Dialog {
    private Context mContext;
    private EditText patientName;
    private EditText patientAutoId;

    // 생년
    private EditText patientBornYear;
//    private NumberPicker patientYear;
//    private String minYear;
//    private String maxYear;

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
        patientBornYear = findViewById(R.id.edit_patient_born_year);      // 직접 생년 입력
//        patientYear = findViewById(R.id.picker_patient_born_year);      // numPicker 이용 생년 입력
        patientAutoId = findViewById(R.id.edit_patient_id);

        manRadioBtn = findViewById(R.id.radioButton);
        womanRadioBtn = findViewById(R.id.radioButton2);
        radioGroup = findViewById(R.id.radioGroup);

        dialogNoBtn = findViewById(R.id.dialog_user_info_no_button);
        dialogYesBtn = findViewById(R.id.dialog_user_info_yes_button);

        // 환자 id 입력 불가 및 환자 id 자동 채번
        patientAutoId.setClickable(false);
        patientAutoId.setFocusable(false);
        patientAutoId.setText(getRandomString(8));
        patientAutoId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "ID는 수정할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // 성별 디폴트 체크(남성)
        radioGroup.check(manRadioBtn.getId());

        // 환자 정보 입력 테스트 시 디폴트 입력값
        testClick();

        // 환자 생년 설정 (numPicker 사용)
//        patientYear.setWrapSelectorWheel(false);
//        patientYear.setMinValue(1920);
//        patientYear.setMaxValue(2500);
//        patientYear.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
//        patientYear.setValue(2000);

        dialogYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseGender();
                String name = patientName.getText().toString();
                String bornYear = patientBornYear.getText().toString();  // 생년 edit로 입력
//                String bornYear = String.valueOf(patientYear.getValue());  // 생년 numPicker로 입력
                String autoId = patientAutoId.getText().toString();
//                String autoId = null;

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

    public String getRandomString(int length) {
        String charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789";
        StringBuilder randomString = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charset.length());
            char randomChar = charset.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }

    public void testClick() {
        patientName.setText("홍길동");
        patientBornYear.setText("2000");
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