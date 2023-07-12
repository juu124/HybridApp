package com.dki.hybridapptest.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Base64;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

public class Utils {
    public static String TAG = "Utils";

    public static boolean getNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static void setDownloadListener(Activity activity, WebView webView) {
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    MimeTypeMap mtm = MimeTypeMap.getSingleton();
                    DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);

                    Uri downloadUri = Uri.parse(url);
                    // 파일 이름을 추출한다. contentDisposition에 filename이 있으면 그걸 쓰고 없으면 URL의 마지막 파일명을 사용한다.
                    String fileName = downloadUri.getLastPathSegment();
                    int pos = 0;
                    contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8"); //디코딩
                    if ((pos = contentDisposition.toLowerCase().lastIndexOf("filename=")) >= 0) {
                        fileName = contentDisposition.substring(pos + 9);
                        pos = fileName.lastIndexOf(";");
                        if (pos > 0) {
                            fileName = fileName.substring(0, pos - 1);
                        }
                    }

                    // MIME Type을 확장자를 통해 예측한다.
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
                    String mimeType = mtm.getMimeTypeFromExtension(fileExtension);

                    fileName = fileName.replaceAll("\"", "");
                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                    request.setTitle(fileName);
                    request.setDescription("Downloading file...");
                    request.setNotificationVisibility(1);
                    request.setMimeType(mimeType);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();

                    Toast.makeText(activity, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show();
                    // 다운로드 매니저에 요청 등록
                    downloadManager.enqueue(request);
                    activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                } catch (Exception e) {
                    if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            Toast.makeText(activity, "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
                        } else {
                            Toast.makeText(activity, "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
                        }
                    }
                }
            }

            BroadcastReceiver onComplete = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Toast.makeText(activity, "다운로드가 완료되었어요.", Toast.LENGTH_SHORT).show();
                }
            };
        });
    }

    public static String getFacetID(Context context) {
        try {
            // RPClient PackageInfo 획득
            PackageInfo info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            // RPClient를 빌드 할 때 이용한 인증서 획득
            byte[] cert = info.signatures[0].toByteArray();
            InputStream input = new ByteArrayInputStream(cert);
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            X509Certificate c = (X509Certificate) cf.generateCertificate(input);
            // Sha1 Hash(빌드 할 때 이용 한 인증서)
            MessageDigest md = MessageDigest.getInstance("SHA1");

            // FIDO1.0 FacetID 형식에 맞추어 만듦
            String rpfacetinfo = "android:apk-key-hash:"
                    + Base64.encodeToString(md.digest(c.getEncoded()),
                    Base64.DEFAULT | Base64.NO_WRAP | Base64.NO_PADDING);
            return rpfacetinfo;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    //컬러 코드로 변경(예: #373737)
    public static final void updateStatusBarColor(Activity context, String color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(color));
        }
    }

    /*****************************************
     * 사용자 Static Method
     *****************************************/
    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
            if (pkgName.equalsIgnoreCase(context.getPackageName())) {
                String className = resolveInfo.activityInfo.name;
                return className;
            }
        }
        return null;
    }

    //배지 카운트 업데이트
    public static void updateIconBadge(Context context, int notiCnt) {
        Intent badgeIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        badgeIntent.putExtra("badge_count", notiCnt);
        badgeIntent.putExtra("badge_count_package_name", context.getPackageName());
        badgeIntent.putExtra("badge_count_class_name", getLauncherClassName(context));
        context.sendBroadcast(badgeIntent);
    }

    //cookie를 지우기 위한 clearCookies
    public static void clearCookies(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            GLog.d("Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else {
            GLog.d("Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    public static boolean isEnrolledFingerprint(Context context) {
        Object obj = null;
        try {
            FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            Method method = FingerprintManager.class.getDeclaredMethod("getEnrolledFingerprints");
            obj = method.invoke(fingerprintManager);

        } catch (Exception e) {
            GLog.d("error : " + e.getMessage());
        }

        int size = ((List) obj).size();

        if (size > 0) {
            return true;
        } else {
            return false;
        }
    }

    public static void clearBadgeCount(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }
        }
    }

    //앱버전 명
    public static String getAppVersionName(Context context) {
        PackageInfo packageInfo = null;         //패키지에 대한 전반적인 정보

        //PackageInfo 초기화
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        return packageInfo.versionName;
    }

    //앱버전 체크 (같은 버전 : 0, 최신버전 : 1, 구버전 : -1 값 반환)
    public static int compareVersionNames(String oldVersionName, String newVersionName) {
        int res = 0;

        String[] oldNumbers = oldVersionName.split("\\.");
        String[] newNumbers = newVersionName.split("\\.");

        // To avoid IndexOutOfBounds
        int maxIndex = Math.min(oldNumbers.length, newNumbers.length);

        for (int i = 0; i < maxIndex; i++) {
            int oldVersionPart = Integer.valueOf(oldNumbers[i]);
            int newVersionPart = Integer.valueOf(newNumbers[i]);

            if (oldVersionPart < newVersionPart) {
                res = -1;
                break;
            } else if (oldVersionPart > newVersionPart) {
                res = 1;
                break;
            }
        }

        // If versions are the same so far, but they have different length...
        if (res == 0 && oldNumbers.length != newNumbers.length) {
            res = (oldNumbers.length > newNumbers.length) ? 1 : -1;
        }

        return res;
    }
}
