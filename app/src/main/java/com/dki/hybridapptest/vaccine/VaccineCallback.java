package com.dki.hybridapptest.vaccine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Window;
import android.widget.Toast;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.R;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.ui.activity.IntroActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dreamsecurity.magic_mvaccine.MagicResult;
import com.dreamsecurity.magic_mvaccine.MagicmVaccine;
import com.dreamsecurity.magic_mvaccine.MagicmVaccineListener;
import com.dreamsecurity.magic_mvaccine.ScanData;
import com.dreamsecurity.magic_mvaccine.exception.MagicNullInstance;

import java.util.ArrayList;

// USE_VACCINE_DREAM
public class VaccineCallback implements MagicmVaccineListener {

    private Context context;
    private StringBuilder sb;
    private CustomDialog customDialog;

    public VaccineCallback(Context context) {
        this.context = context;
    }

    /**
     * 백신 검사 진행 결과 성공인 경우 콜백
     *
     * @param magicResult 백신 검사 결과
     */
    @Override
    public void OnSuccess(MagicResult magicResult) {
//        swToast(magicResult);

        if (context instanceof IntroActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ((IntroActivity) context).vaccineResult(true);
            }
        }

        try {
            MagicmVaccine.getInstance().stopVaccine();
            if (context instanceof IntroActivity) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ((IntroActivity) context).setRealTimeScanRunning(false);
                }
            }
        } catch (MagicNullInstance magicNullInstance) {
            magicNullInstance.printStackTrace();
        }

        switch (magicResult) {
            case RESULT_OK:
                GLog.d("검사완료 - 악성코드 없음");
                Toast.makeText(context, "백신 검사 완료 악성 코드 없음", Toast.LENGTH_SHORT).show();
                // 악성코드 검출되지 않음
                break;
            case MALWARE_DELETE_SUCCESS:
                GLog.d("검사완료 - 악성코드 삭제 성공");
                Toast.makeText(context, "백신 검사 완료 악성 코드 삭제 성공", Toast.LENGTH_SHORT).show();
                // 악성코드 삭제 성공
                break;
        }
    }

    /**
     * 백신 검사 진행 결과 실패인 경우 콜백
     *
     * @param magicResult 백신 검사 결과
     */
    @Override
    public void OnFailure(MagicResult magicResult) {

        swToast(magicResult);

        if (context instanceof IntroActivity) {
            ((IntroActivity) context).vaccineResult(false);
        }

        try {
            MagicmVaccine.getInstance().stopVaccine();
            if (context instanceof IntroActivity) {
                ((IntroActivity) context).setRealTimeScanRunning(false);
            }
        } catch (MagicNullInstance magicNullInstance) {
            magicNullInstance.printStackTrace();
        }

        switch (magicResult) {
            case UPDATE_ERROR_NETWORKT:
                // 네트워크 에러
                break;
            case UPDATE_ERROR_NETWORK_TIME_OUT:
                // 네트워크 시간초과
                break;
            case UPDATE_ERROR_LICENSE:
                // 라이선스 검증 실패
                break;
            case UPDATE_ERROR_UNKNOWN:
                // 패턴 업데이트 실패
                break;
        }

    }

    /**
     * 백신 검사 진행 결과 루팅된 단말로 탐지 및 악성코드 탐지된 경우 콜백
     *
     * @param magicResult 백신 검사 결과
     * @param scanData    백신 실행 결과 검출된 악성코드 리스트
     */
    @Override
    public void OnDetected(MagicResult magicResult, final ArrayList<ScanData> scanData) {
        sb = new StringBuilder();
        sb.append(magicResult.getResultName()).append(" : ").append(magicResult.getMsg());

        if (scanData != null) {
            sb.append("\n").append("악성코드 개수 : ").append(scanData.size());
        }

//        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();

        switch (magicResult) {
            case ROOTING_DEVICE:
                // 루팅된 단말
                customDialog = new CustomDialog(context, new CustomDialogClickListener() {

                    @Override
                    public void onPositiveClick(String text) {

                    }

                    @Override
                    public void onNegativeClick() {


                    }
                }, "안내", context.getResources().getString(R.string.vaccine_rooting_error_message), Constant.ONE_BUTTON, true);
                customDialog.setTwoButtonText("취소", "확인");
                customDialog.setCancelable(false);
                customDialog.show();

                setDisplay(customDialog);

                break;
            case DETECTED:
                // 악성코드 검출
                customDialog = new CustomDialog(context, new CustomDialogClickListener() {
                    @Override
                    public void onPositiveClick(String text) {
                        try {
                            MagicmVaccine.getInstance().deleteScanData(scanData);
                        } catch (MagicNullInstance magicNullInstance) {
                            magicNullInstance.printStackTrace();
                        }
                    }

                    @Override
                    public void onNegativeClick() {
                        ((Activity) context).finish();
                    }
                }, "안내", context.getResources().getString(R.string.vaccine_malware_error_message), Constant.TWO_BUTTON, true);
                customDialog.setTwoButtonText("예", "앱 종료");
                customDialog.setCancelable(false);
                customDialog.show();
                setDisplay(customDialog);
                break;
            case USER_CANCEL:
                // 사용자 취소
                break;
            case REMAINED_MALWARE_DATA:
                // 악성코드 데이터가 남아있음
                break;
        }
    }

    //커스텀 팝업의 사이즈를 조절
    public void setDisplay(CustomDialog customDialog) {
        Display display = ((IntroActivity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = customDialog.getWindow();
        int x = (int) (size.x * 0.9f);
        int y = (int) (size.y * 0.7f);

        window.setLayout(x, y);
    }

    /**
     * show toast msg
     */
    private void swToast(MagicResult magicResult) {
        sb = new StringBuilder();
        sb.append(magicResult.getResultName()).append(" : ").append(magicResult.getMsg());
        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
    }

    /**
     * show alert dialog
     */
    private void swAlertDialog(String msg, DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
        new AlertDialog.Builder(context, R.style.MyDialogTheme)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("확인", positive)
                .setNegativeButton("취소", negative).show();
    }

}
