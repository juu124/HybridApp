package com.dki.hybridapptest.activities;

import android.os.Bundle;
import android.widget.RadioGroup;

import com.dki.hybridapptest.R;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

public class XSignSettingActivity extends XSignBaseActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup mDebugLevelGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xsign_setting);
        setActivityTitle("MagicXSign Setting");
        initSetting();
    }

    /**
     * 설정화면 초기 구성
     */
    private void initSetting() {
        int nDebugLevel = getDebugSettingValue();
        mDebugLevelGroup = (RadioGroup) findViewById(R.id.debug_level_group);
        mDebugLevelGroup.setOnCheckedChangeListener(this);

        switch (nDebugLevel) {
            case MagicXSign_Type.XSIGN_DEBUG_LEVEL_0:
                mDebugLevelGroup.check(R.id.debug_level0);
                break;
            case MagicXSign_Type.XSIGN_DEBUG_LEVEL_1:
                mDebugLevelGroup.check(R.id.debug_level1);
                break;
            case MagicXSign_Type.XSIGN_DEBUG_LEVEL_2:
                mDebugLevelGroup.check(R.id.debug_level2);
                break;
            case MagicXSign_Type.XSIGN_DEBUG_LEVEL_3:
                mDebugLevelGroup.check(R.id.debug_level3);
                break;
        }

    }

    /**
     * 설정화면의 Debug Level 변경 및 저장
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        if (group == mDebugLevelGroup) {
            switch (checkedId) {
                case R.id.debug_level0:
                    saveSettingValue(SET_KEY_DEBUG_LEVEL, MagicXSign_Type.XSIGN_DEBUG_LEVEL_0);
                    break;
                case R.id.debug_level1:
                    saveSettingValue(SET_KEY_DEBUG_LEVEL, MagicXSign_Type.XSIGN_DEBUG_LEVEL_1);
                    break;
                case R.id.debug_level2:
                    saveSettingValue(SET_KEY_DEBUG_LEVEL, MagicXSign_Type.XSIGN_DEBUG_LEVEL_2);
                    break;
                case R.id.debug_level3:
                    saveSettingValue(SET_KEY_DEBUG_LEVEL, MagicXSign_Type.XSIGN_DEBUG_LEVEL_3);
                    break;
            }
        }
    }

}
