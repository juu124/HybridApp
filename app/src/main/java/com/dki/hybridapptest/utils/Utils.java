package com.dki.hybridapptest.utils;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.MimeTypeMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Utils {
    public static String TAG = "Utils";
    public static final int KHMER_BEGIN_UNICODE = 6016;
    public static final int KHMER_END_UNICODE = 6143;
    public static final int HANGUL_BEGIN_UNICODE = 44032; // 가
    public static final int HANGUL_END_UNICODE = 55203; // 힣
    public static final int HANGUL_BASE_UNIT = 588;
    public static final int unicodeA = 65;
    public static final int unicodeZ = 90;

    public static final int[] INITIAL_SOUND_UNICODE = {12593, 12594, 12596,
            12599, 12600, 12601, 12609, 12610, 12611, 12613, 12614, 12615,
            12616, 12617, 12618, 12619, 12620, 12621, 12622};

    public static final char[] INITIAL_SOUND = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
            'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅃ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};

    public static boolean getNetworkStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String encodeBase64String(String data) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static String encodeBase64String(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    public static ArrayList distinct(List dataList) {
        ArrayList resultList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (!resultList.contains(dataList.get(i))) {
                resultList.add(dataList.get(i));
            }
        }
        return resultList;
    }

    // 모든 권한 검사
    public static boolean isGrantedAllPermission(Context context) {
        GLog.d("isGrantedAllPermission");
        for (String permission : getPermissionListAll()) {
            GLog.d("permission ===== " + permission);
            if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        GLog.d("isGrantedAllPermission success");
        return true;
    }

    // 파일을 저장하려는 폴더가 존재하지 않을 경우 생성하여 반환
    public static File makeDirectory(Context context) {
        GLog.i("call :: makeDirectory");
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dir = new File(context.getCacheDir().getPath() + File.separator + Constant.FILE_CACHE_DIRECTORY_NAME);
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constant.FILE_CACHE_DIRECTORY_NAME);
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }

    // 저장 되어 있는 임시 img 파일을 삭제
    public static void clearCacheFiles(Context context) {
        GLog.i("call :: clearCacheFiles");
        File dir;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dir = new File(context.getCacheDir().getPath() + File.separator + Constant.FILE_CACHE_DIRECTORY_NAME);
        } else {
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constant.FILE_CACHE_DIRECTORY_NAME);
        }

        GLog.i("SDK_INT_OVER dir == " + dir);
        clearCacheRecursively(dir);
    }

    // 파일 캐시 삭제
    public static void clearCacheRecursively(File directory) {
        GLog.i("call :: clearCacheRecursively ==== " + directory.isDirectory());

        if (directory.isDirectory()) {
            GLog.i("call :: clearCacheRecursively  1");
            File[] files = directory.listFiles();
            for (int i = 0; i < files.length; i++) {

                GLog.i("call :: clearCacheRecursively  2");
                File file = files[i];
                if (file.isDirectory()) {

                    GLog.i("call :: clearCacheRecursively  3");
                    if (TextUtils.equals(file.getName(), Constant.FILE_CACHE_DIRECTORY_NAME)) {

                        GLog.i("call :: clearCacheRecursively  4");
                        File[] caches = directory.listFiles();
                        for (int j = 0; j < caches.length; j++) {

                            GLog.i("call :: clearCacheRecursively  5");
                            File cache = caches[j];
                            cache.delete();
                        }
                    } else {
                        // 하위 directory 일 경우
                        clearCacheRecursively(file);
                    }
                } else {
                    // 파일 삭제
                    file.delete();
                }
            }
        }
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    // 화면 캡처
    public static boolean screenCapture(View root) {
        if (root == null) return false;
        root.setDrawingCacheEnabled(true);
        root.buildDrawingCache();
        // 루트뷰의 캐시를 가져옴
        Bitmap screenshot = root.getDrawingCache();

        // get view coordinates
        int[] location = new int[2];
        root.getLocationInWindow(location);

        // 이미지를 자를 수 있으나 전체 화면을 캡쳐 하도록 함
        Bitmap bmp = Bitmap.createBitmap(screenshot, location[0], location[1], root.getWidth(), root.getHeight(), null, false);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".png";

        if (android.os.Build.VERSION.SDK_INT >= 28) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/*");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
            }

            ContentResolver contentResolver = root.getContext().getContentResolver();
            Uri item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            try {
                ParcelFileDescriptor pfd = contentResolver.openFileDescriptor(item, "w", null);

                if (pfd != null) {

                    byte[] bmpToByte = Utils.bitmapToByteArray(bmp);

                    FileOutputStream fos = new FileOutputStream(pfd.getFileDescriptor());
                    fos.write(bmpToByte);
                    fos.close();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear();
                        values.put(MediaStore.Images.Media.IS_PENDING, 0);
                        contentResolver.update(item, values, null, null);
                    }
                }
            } catch (FileNotFoundException e) {
                GLog.e("FileNotFoundException == " + e.getMessage());
//                com.dkitec.redwoodhealth.utils.RedwoodLog.printStackTrace(e);
            } catch (IOException e) {
                GLog.e("IOException == " + e.getMessage());
//                com.dkitec.redwoodhealth.utils.RedwoodLog.printStackTrace(e);
            }

        } else {

            String strFolderPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
            File folder = new File(strFolderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String strFilePath = strFolderPath + "/" + imageFileName;
            File fileCacheItem = new File(strFilePath);
            OutputStream out = null;
            try {
                fileCacheItem.createNewFile();
                out = new FileOutputStream(fileCacheItem);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out);

                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intent.setData(Uri.fromFile(fileCacheItem));
                root.getContext().sendBroadcast(intent);

            } catch (Exception e) {
                GLog.e("Exception == " + e.getMessage());
//                com.dkitec.redwoodhealth.utils.RedwoodLog.printStackTrace(e);
                return false;
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    GLog.e("IOException == " + e.getMessage());
//                    com.dkitec.redwoodhealth.utils.RedwoodLog.printStackTrace(e);
                }
            }
        }
        root.setDrawingCacheEnabled(false);
        return true;
    }

    // 권한 설정
    public static ArrayList<String> getPermissionListAll() {
        ArrayList<String> permissionList = new ArrayList<>();
        permissionList.add(Manifest.permission.READ_CONTACTS);
        permissionList.add(Manifest.permission.CAMERA);
//        permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
            // 외부 저장소 허용, R 이상은 권한 불필요
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 파일 다운로드 필요 권한 버전 티라미수 이상
            permissionList.add(Manifest.permission.READ_MEDIA_AUDIO);
            permissionList.add(Manifest.permission.READ_MEDIA_VIDEO);
            permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            // 파일 다운로드 필요 권한
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        return permissionList;
    }

    // 파일 다운로드
    public static void fileDownload(Context context, String downUrl, String destFileName) {
        try {
            MimeTypeMap mtm = MimeTypeMap.getSingleton();
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

            Uri downloadUri = Uri.parse(downUrl);
            String fileName = downloadUri.getLastPathSegment();

            // MIME Type을 확장자를 통해 예측한다.
            String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            String mimeType = mtm.getMimeTypeFromExtension(fileExtension);

            fileName = fileName.replaceAll("\"", "");
            GLog.d("fileDownload====== fileName : " + fileName);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            if (TextUtils.isEmpty(destFileName)) {
                GLog.d("fileDownload====== fileName : " + fileName);
                request.setTitle(fileName);
                request.setDescription("Downloading file...");
                request.setNotificationVisibility(1);
                request.setMimeType(mimeType);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
            } else {
                GLog.d("fileDownload====== destFileName : " + destFileName);
                request.setTitle(destFileName);
                request.setDescription("Downloading file...");
                request.setNotificationVisibility(1);
                request.setMimeType(mimeType);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, destFileName);
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
            }
            // 다운로드 매니저에 요청 등록
            downloadManager.enqueue(request);
        } catch (Exception e) {
            GLog.e("Exception : " + e.toString());
        }
    }

    // 파일 다운로드 리스너
//    public static void setDownloadListener(Activity activity, WebView webView) {
//        GLog.d();
//        webView.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                try {
//
//                    GLog.d("url ===" + url);
//                    MimeTypeMap mtm = MimeTypeMap.getSingleton();
//                    DownloadManager downloadManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
//
//                    Uri downloadUri = Uri.parse(url);
//                    // 파일 이름을 추출한다. contentDisposition에 filename이 있으면 그걸 쓰고 없으면 URL의 마지막 파일명을 사용한다.
//                    String fileName = downloadUri.getLastPathSegment();
//                    int pos = 0;
//                    contentDisposition = URLDecoder.decode(contentDisposition, "UTF-8"); //디코딩
//                    if ((pos = contentDisposition.toLowerCase().lastIndexOf("filename=")) >= 0) {
//                        fileName = contentDisposition.substring(pos + 9);
//                        pos = fileName.lastIndexOf(";");
//                        if (pos > 0) {
//                            fileName = fileName.substring(0, pos - 1);
//                        }
//                    }
//
//                    // MIME Type을 확장자를 통해 예측한다.
//                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
//                    String mimeType = mtm.getMimeTypeFromExtension(fileExtension);
//
//                    fileName = fileName.replaceAll("\"", "");
//                    DownloadManager.Request request = new DownloadManager.Request(downloadUri);
//                    request.setTitle(fileName);
//                    request.setDescription("Downloading file...");
//                    request.setNotificationVisibility(1);
//                    request.setMimeType(mimeType);
//                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
//
//                    Toast.makeText(activity, "다운로드를 시작합니다.", Toast.LENGTH_SHORT).show();
//                    // 다운로드 매니저에 요청 등록
//                    downloadManager.enqueue(request);
//                    activity.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//                } catch (Exception e) {
//                    if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                            Toast.makeText(activity, "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
//                        } else {
//                            Toast.makeText(activity, "다운로드를 위해\n권한이 필요합니다.", Toast.LENGTH_LONG).show();
//                            ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1004);
//                        }
//                    }
//                }
//            }
//
//            BroadcastReceiver onComplete = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    Toast.makeText(activity, "다운로드가 완료되었어요.", Toast.LENGTH_SHORT).show();
//                }
//            };
//        });
//    }

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

    public static String getHangulInitialSound(String value) {

        StringBuffer resultBuffer = new StringBuffer();

        int unicode = convertStringToUnicode(value);

        if (HANGUL_BEGIN_UNICODE <= unicode
                && unicode <= HANGUL_END_UNICODE) {
            int tmp = (unicode - HANGUL_BEGIN_UNICODE);
            int index = tmp / HANGUL_BASE_UNIT;
            resultBuffer.append(INITIAL_SOUND[index]);
        } else {
            resultBuffer.append(convertUnicodeToChar(unicode));
        }

        String result = resultBuffer.toString().toUpperCase();

        int unicodeInitial = Utils.convertStringToUnicode(resultBuffer.toString().toUpperCase());
        if ((unicodeInitial >= unicodeA && unicodeInitial <= unicodeZ) || (unicodeInitial >= KHMER_BEGIN_UNICODE && unicodeInitial <= KHMER_END_UNICODE)) {
            result = "#";
        }

        return result;
    }

    public static char convertUnicodeToChar(int unicode) {
        if (unicode < 65) {
            return '#';
        } else {
            return convertUnicodeToChar(toHexString(unicode));
        }
    }

    public static char convertUnicodeToChar(String hexUnicode) {
        return (char) Integer.parseInt(hexUnicode, 16);
    }

    private static String toHexString(int decimal) {
        Long intDec = Long.valueOf(decimal);
        return Long.toHexString(intDec);
    }

    public static int convertStringToUnicode(String str) {

        int unicodeList = 0;

        if ((str != null) && (str.length() > 0)) {
            str.toUpperCase();
            unicodeList = convertCharToUnicode(str.charAt(0));
        }

        return unicodeList;
    }

    public static int convertCharToUnicode(char ch) {
        return (int) ch;
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
