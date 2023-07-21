package com.dki.hybridapptest.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;
import com.dki.hybridapptest.bridge.AndroidBridge;
import com.dki.hybridapptest.utils.GLog;

public class MoveWebViewActivity extends AppCompatActivity {
    private EditText editUrl;
    private Button btnMoveUrl;
    private RadioButton radioBtnFullMode;
    private RadioButton radioBtnHalfMode;
    private RadioGroup radioGroup;
    private AndroidBridge mAndroidBridge;
    private String url;
    private boolean isFullMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_move_web_view);

        editUrl = findViewById(R.id.edit_url);
        btnMoveUrl = findViewById(R.id.btn_move_url);

        radioGroup = findViewById(R.id.radio_btn_layout);
        radioBtnFullMode = findViewById(R.id.radio_btn_full_mode);
        radioBtnHalfMode = findViewById(R.id.radio_btn_half_mode);

        radioGroup.check(radioBtnFullMode.getId());

        btnMoveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = editUrl.getText().toString();
                onRadioButtonClicked();
                Toast.makeText(MoveWebViewActivity.this, "웹뷰 이동", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void onRadioButtonClicked() {
        if (radioBtnFullMode.isChecked()) {
            isFullMode = true;
            GLog.d("isFullMode === " + isFullMode);
            mAndroidBridge = new AndroidBridge(MoveWebViewActivity.this, url, isFullMode);
        } else if (radioBtnHalfMode.isChecked()) {
            isFullMode = false;
            GLog.d("isFullMode === " + isFullMode);
            mAndroidBridge = new AndroidBridge(MoveWebViewActivity.this, url, isFullMode);
        } else {
            Toast.makeText(this, "화면 모드를 선택해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

}