package com.dki.hybridapptest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dki.hybridapptest.R;

public class MoveWebViewActivity extends AppCompatActivity {
    private EditText editUrl;
    private Button btnMoveUrl;
    private RadioButton radioBtnFullMode;
    private RadioButton radioBtnHalfMode;
    private RadioGroup radioGroup;

    // 웹뷰 url, 크기, 이동
    private String url;
    private boolean isFullMode = true;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면 캡쳐 방지
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_move_web_view);


        editUrl = findViewById(R.id.edit_url);
        btnMoveUrl = findViewById(R.id.btn_move_url);
        radioGroup = findViewById(R.id.radio_btn_layout);
        radioBtnFullMode = findViewById(R.id.radio_btn_full_mode);
        radioBtnHalfMode = findViewById(R.id.radio_btn_half_mode);

        radioGroup.check(radioBtnFullMode.getId());  // 체크박스에서 radioBtnFullMode가 디폴트로 체크된 상태

        btnMoveUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = editUrl.getText().toString();
                moveWebView();
            }
        });
    }

    // 크기 버튼 체크
    public void moveWebView() {
        if (!TextUtils.isEmpty(url)) { // url 값 있음
            if (radioBtnFullMode.isChecked()) { // 풀 모드
                isFullMode = true;
            } else if (radioBtnHalfMode.isChecked()) { // 하프 모드
                isFullMode = false;
            } else {
                Toast.makeText(this, "화면 모드를 선택해주세요.", Toast.LENGTH_SHORT).show();
            }
            mIntent = new Intent(this, WebViewSizeChangeActivity.class);
            mIntent.putExtra("url", url);
            mIntent.putExtra("displaySize", isFullMode);
            startActivity(mIntent);
        } else { // url 값 없음
            Toast.makeText(this, "url 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
    }
}