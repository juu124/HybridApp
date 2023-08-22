package com.dki.hybridapptest.vaccine;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.dki.hybridapptest.Interface.CustomDialogClickListener;
import com.dki.hybridapptest.dialog.CustomDialog;
import com.dki.hybridapptest.ui.activity.IntroActivity;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.dreamsecurity.magic_mvaccine.MagicmVaccine;
import com.dreamsecurity.magic_mvaccine.exception.MagicConnectivityException;
import com.dreamsecurity.magic_mvaccine.exception.MagicEssentialPermissionException;
import com.dreamsecurity.magic_mvaccine.exception.MagicIOException;
import com.dreamsecurity.magic_mvaccine.exception.MagicIntegrityCheckException;
import com.dreamsecurity.magic_mvaccine.exception.MagicLicenseExpireException;
import com.dreamsecurity.magic_mvaccine.exception.MagicLicenseInvalidException;
import com.dreamsecurity.magic_mvaccine.exception.MagicLicenseKeyNotFoundException;
import com.dreamsecurity.magic_mvaccine.exception.MagicLicenseOverException;
import com.dreamsecurity.magic_mvaccine.exception.MagicNullInstance;
import com.dreamsecurity.magic_mvaccine.exception.MagicNullPointerException;
import com.dreamsecurity.magic_mvaccine.exception.MagicTimeoutException;
import com.dreamsecurity.magic_mvaccine.option.MagicmVaccineOptions;

// USE_VACCINE_DREAM
public class VaccineManager {

    private boolean createInstanceSuccess;

    private String TAG = "VaccineManager";
    private Activity mActivity;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public VaccineManager(Activity activity) {
        this.mActivity = activity;
        try {
            createMagic_mVaccineInstance(mActivity);
        } catch (RuntimeException e) {

            GLog.e("error : " + e.getMessage());

//            Handler mHandler = new Handler(Looper.getMainLooper());
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    CustomDialog customDialog = new CustomDialog(mActivity, new CustomDialogClickListener() {
                        @Override
                        public void onPositiveClick(String text) {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }

                        @Override
                        public void onNegativeClick() {


                        }
                    }, "안내", "네트워크에 연결되지 않았습니다. \n네트워크 연결 상태를 확인 후 진행해 주세요.", Constant.ONE_BUTTON, true);
                    customDialog.setOneButtonText("확인");
                    customDialog.setCancelable(false);
                    customDialog.show();

//                    Display display = mActivity.getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//
//                    Window window = customDialog.getWindow();
//
//                    int x = (int) (size.x * 0.9f);
//                    int y = (int) (size.y * 0.7f);
//
//                    window.setLayout(x, y);
                }
            }, 0);
        }
    }

    private void startService() {

        GLog.d("startService.......");
        int scanLevel = 0;
//        if (VaccineSettings.isRootingCheck) {
//            scanLevel |= MagicmVaccineOptions.ROOTING_CHECK;
//        }
        //릴리즈 버전일때만 루팅체크
        if (!Constant.IS_DEBUG) {
            scanLevel |= MagicmVaccineOptions.ROOTING_CHECK;
        }

        if (VaccineSettings.isMalwareScan) {
            scanLevel |= MagicmVaccineOptions.MALWARE_SCAN;
        }

        boolean useRealTimeScan = VaccineSettings.isRealTimeScan;
        boolean useIntro = VaccineSettings.isUseIntro;
        boolean useProgressBar = VaccineSettings.isUseProgress;
        boolean passUpdateResult = VaccineSettings.isPassUpdateResult;
        boolean useResultUI = VaccineSettings.isUseResultUi;
        boolean useNoti = VaccineSettings.isUseNoti;

        if (scanLevel == 0) {
            Toast.makeText(mActivity, "적어도 한 개의 검사 진행 단계를 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 백신 사용 옵션 클래스 생성
        MagicmVaccineOptions options = new MagicmVaccineOptions.Builder(
                scanLevel, useRealTimeScan)
                // 백신 검사 시작 시 Intro 팝업 사용 여부를 설정
                .setUseIntro(useIntro)
                // 백신 검사 시작 시 Notification 사용 여부를 설정
                .setUseNotification(useNoti)
                // 악성코드 검사 진행률에 대한 ProgressBar 사용 여부를 설정
                .setUseProgressBar(useProgressBar)
                // 패턴 업데이트 결과에 관계없이 다음 프로세스를 진행할지 여부를 설정
                .setPassUpdateResult(passUpdateResult)
                // 악성코드 검출 후 결과를 SDK 내의 UI 로 띄울지 여부를 설정
                .setUseResultUI(useResultUI)
                // 설정된 옵션 값으로 MagicmVaccineOptions 인스턴스를 생성
                .build();

        // 백신 진행 결과 콜백 클래스 생성
        VaccineCallback callback = new VaccineCallback(mActivity);

        try {
            // 백신 검사 시작
            boolean result = MagicmVaccine.getInstance().startVaccine(options, callback);

            // 백신 시작 성공 & 실시간 검사 사용 true
            // 샘플 앱의 start vaccine 버튼 stop vaccine 버튼으로 토글
            if (result && useRealTimeScan) {
//                setRealTimeScanRunning(true);
                if (mActivity instanceof IntroActivity) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ((IntroActivity) mActivity).setRealTimeScanRunning(true);
                    }
                }
            }
        } catch (MagicNullInstance magicNullInstance) {
            magicNullInstance.printStackTrace();
        }
    }


    /**
     * MagicmVaccine 인스턴스 생성
     *
     * @param context android context
     */
    private void createMagic_mVaccineInstance(Context context) {

        try {
            createInstanceSuccess = MagicmVaccine.createInstance(context);
            startService();
        } catch (MagicNullPointerException e) {
            // Context 가 null
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });
        } catch (MagicIOException e) {
            // 앱 내에 탑재된 엔진이 정상적인 엔진파일이 아님
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicTimeoutException e) {
            // 엔진파일의 무결성 체크과정이 일정시간(5초)이상으로 지연
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });
        } catch (MagicEssentialPermissionException e) {
            // READ_PHONE_STATE 권한 허용되지 않음
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicConnectivityException e) {
            // 네트워크 연결 실패
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicIntegrityCheckException e) {
            // 엔진파일 무결성 검사 실패
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicLicenseExpireException e) {
            // 라이선스 기간 만료
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicLicenseOverException e) {
            // 라이선스 당 사용 갯수를 초과
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicLicenseInvalidException e) {
            // 라이선스 검증 실패
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        } catch (MagicLicenseKeyNotFoundException e) {
            // 라이선스 파일 누락
            e.printStackTrace();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    GLog.d("error == " + e.getMessage());
                }
            });

        }
    }

    public void stopVaccine() {
        try {
            // 앱 종료 시 실시간 검사 종료
            MagicmVaccine.getInstance().stopVaccine();
        } catch (MagicNullInstance magicNullInstance) {
            magicNullInstance.printStackTrace();
        }
    }
}
