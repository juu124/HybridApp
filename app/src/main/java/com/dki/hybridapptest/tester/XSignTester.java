package com.dki.hybridapptest.tester;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.dreamsecurity.dstoolkit.License;
import com.dreamsecurity.magicxsign.MagicXSign;
import com.dreamsecurity.magicxsign.MagicXSign_Err;
import com.dreamsecurity.magicxsign.MagicXSign_Exception;
import com.dreamsecurity.magicxsign.MagicXSign_Type;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;

public class XSignTester {

    /**
     * Called when the activity is first created.
     */
    private MagicXSign Xsign = null;
    private Context mContext;

    private byte[] mbinSampleSignPri = null;  // 테스트 서명용 인증서 개인키
    private byte[] mbinSampleSignCert = null;  // 테스트 서명용 인증서

    private byte[] mbinSampleKmPri = null;  // 테스트 암호용 인증서 개인키
    private byte[] mbinSampleKmCert = null;  // 테스트 암호용 인증서

    private byte[] mbinRootCACert = null;  // 테스트 RootCA 인증서
    private byte[] mbinCACert = null;  // 테스트 CA 인증서

    private String mTestCertPassword = "qwer1234";        // 테스트 인증서 비밀번호
    private String mTestIDN = "7001011112121";    // 테스트 인증서 DN

    private int iPkiType = MagicXSign_Type.XSIGN_PKI_TYPE_NPKI | MagicXSign_Type.XSIGN_PKI_TYPE_GPKI | MagicXSign_Type.XSIGN_PKI_TYPE_PPKI | MagicXSign_Type.XSIGN_PKI_TYPE_MPKI;
    private String OID_FILTER = "1.2.410.200005.1.1.5,1.2.410.200005.1.1.6.8,1.2.410.200004.5.1.1.7,1.2.410.200004.5.3.1.1,1.2.410.200004.5.3.1.2,1.2.410.200004.5.2.1.1,1.2.410.200004.5.2.1.3,1.2.410.200004.5.2.1.5,1.2.410.200004.5.2.1.5.12,1.2.410.200004.5.2.1.5.28,1.2.410.200004.5.2.1.5.30,1.2.410.200004.5.2.1.5.32,1.2.410.200004.5.2.1.6.114,1.2.410.200004.5.2.1.6.115,1.2.410.200004.5.2.1.6.183,1.2.410.200004.5.2.1.6.229,1.2.410.200004.5.2.1.6.237,1.2.410.200004.5.2.1.6.245,1.2.410.200004.5.2.1.6.246,1.2.410.200004.5.2.1.6.250,1.2.410.200004.5.2.1.6.251,1.2.410.200004.5.2.1.6.254,1.2.410.200004.5.2.1.6.48,1.2.410.200004.5.2.1.6.70,1.2.410.200004.5.2.1.6.73,1.2.410.200004.5.2.1.6.90,1.2.410.200004.5.2.1.6.93,1.2.410.200004.5.2.1.6.99,1.2.410.200004.5.2.1.6.257,1.2.410.200004.5.2.1.6.255,1.2.410.200004.5.2.1.6.65,1.2.410.200004.5.2.1.6.221,1.2.410.200004.5.2.1.6.249,1.2.410.200004.5.2.1.6.252,1.2.410.200004.5.2.1.6.248,1.2.410.200004.5.2.1.6.275,1.2.410.200004.5.2.1.6.235,1.2.410.200004.5.2.1.6.279,1.2.410.200004.5.2.1.6.113,1.2.410.200004.5.2.1.6.284,1.2.410.200004.5.2.1.6.285,1.2.410.200004.5.4.1.2,1.2.410.200004.5.4.1.19,1.2.410.200004.5.4.1.31,1.2.410.200004.5.4.1.35,1.2.410.200004.5.4.1.50,1.2.410.200004.5.4.2.11,1.2.410.200004.5.4.2.60,1.2.410.200004.5.4.2.65,1.2.410.200004.5.4.2.67,1.2.410.200004.5.4.2.69,1.2.410.200004.5.4.2.300,1.2.410.200004.5.4.2.302,1.2.410.200004.5.4.2.304,1.2.410.200004.5.4.2.305,1.2.410.200004.5.4.2.306,1.2.410.200004.5.4.2.307,1.2.410.200004.5.4.2.308,1.2.410.200004.5.4.2.310,1.2.410.200004.5.4.2.64,1.2.410.200004.5.4.2.66,1.2.410.200004.5.4.1.14,1.2.410.200004.5.4.2.42,1.2.410.200004.5.4.2.36,1.2.410.200004.5.4.2.25,1.2.410.200004.5.4.2.312,1.2.410.200004.5.4.2.311,1.2.410.200004.5.4.2.55,1.2.410.200004.5.4.2.313,1.2.410.200004.5.4.2.315,1.2.410.200004.5.4.2.317,1.2.410.200004.5.4.2.318,1.2.410.200004.5.4.2.321,1.2.410.200004.5.4.2.322,1.2.410.200004.5.4.2.323,1.2.410.200004.5.4.2.324,1.2.410.200004.5.4.2.325,1.2.410.200004.5.4.2.326,1.2.410.200004.5.4.2.327,1.2.410.200004.5.4.2.328,1.2.410.200004.5.4.2.329,1.2.410.200004.5.4.2.330,1.2.410.200004.5.4.2.331,1.2.410.200004.5.4.2.332,1.2.410.200004.5.4.2.57,1.2.410.200004.5.4.2.50,1.2.410.200004.5.4.2.80,1.2.410.200004.5.4.2.337,1.2.410.200004.5.4.2.340,1.2.410.200004.5.4.2.343,1.2.410.200004.5.4.2.350,1.2.410.200004.5.4.2.360,1.2.410.200004.5.4.2.367,1.2.410.200004.5.4.2.377,1.2.410.200004.5.4.2.379,1.2.410.200012.1.1.3,1.2.410.200012.5.19.1.1,1.2.410.200012.5.13.1.1,1.2.410.200012.5.1.1.171,1.2.410.200012.5.6.1.31,1.2.410.200012.1.1.411,1.2.410.200012.5.11.1.81,1.2.410.200012.5.21.1.11,1.2.410.200012.5.20.1.21,1.2.410.200012.5.1.1.191,1.2.410.200012.1.1.801,1.2.410.200012.1.1.9,1.2.410.200012.5.18.1.21,1.2.410.200012.5.17.1.11,1.2.410.200012.5.15.1.11,1.2.410.200012.5.4.1.61,1.2.410.200012.1.1.431,1.2.410.200012.1.1.441,1.2.410.200012.1.1.451,1.2.410.200012.1.1.401,1.2.410.200012.5.1.1.321,1.2.410.200012.5.1.1.281,1.2.410.200012.5.1.1.331,1.2.410.200012.5.27.1.1,1.2.410.200012.5.26.1.11,1.2.410.200012.1.1.471,1.2.410.200012.5.4.1.21,1.2.410.200012.1.1.4101,1.2.410.200012.1.1.481,1.2.410.200012.1.1.491,1.2.410.200012.1.1.4111,1.2.410.200012.5.14.1.11,1.2.410.200012.5.14.1.21,1.2.410.200012.5.11.1.101,1.2.410.200012.5.28.1.21,1.2.410.200012.5.18.1.31,1.2.410.200012.5.18.1.33,1.2.410.200012.1.1.4151,1.2.410.200012.5.4.1.141,1.2.410.200004.5.2.1.5001";

    public XSignTester() {
    }

    /**
     * MagicXSign 초기화 수행
     *
     * @param context
     */
    public XSignTester(Context context, int nXSignDebugLevel) {
        mContext = context;
        Xsign = new MagicXSign();
        try {
            License.setInfo(mContext, "GMYxnwgB1tfYJFfEE1XDRDZz+0EmTMpV7Gb9wdiJjFQ4l5SBvKNb/rmgVjkriCSx/FA7vvkPOA3Q7MCxbxSdqXFYobYziOb/WMcVwyGEW6GDVY2HP7PzpMJLxZFtnNtkN5SZpGAVedYIGEjvir2zG7xSLK9VMqMRTy9353ev5RFpPE549hXgPAvnE8k/6R0aH4zwfD1OWYzygypXL0tzUZmhbt6TTOyJsWxipL09+urk1RtmDfun9xsP/2aOkG6/Hy8QYu4u8u+mapT4nH5p9DvpMUk4vwoVYIhS4/DYflgdVe2t9zIRkWZBkHt0wEg7t+t9ruUR5Dv3hsMQqGp9cg==");

            Xsign.Init(mContext, nXSignDebugLevel);

        } catch (MagicXSign_Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * 인증서 리스트에 출력 할 인증서 정보 Class
     * 20.05.26 : 인증서 index, pubKeyAlg 추가
     */
    public class CertDetailInfo/* implements Serializable*/ {
        private int index = 0;
        private String SubjectDN = null;
        private String CA = null;
        private String OID = null;
        private String KeyUsage = null;
        private String ExpirationTo = null;
        private String ExpirationFrom = null;
        private String pubKeyAlg = null;

        public int getIndex() {
            return index;
        }

        public String getSubjectDN() {
            return SubjectDN;
        }

        public String getIssur() {
            return CA;
        }

        public String getOID() {
            return OID;
        }

        public String getKeyUsage() {
            return KeyUsage;
        }

        public String getExpirationTo() {
            return ExpirationTo;
        }

        public String getExpirationFrom() {
            return ExpirationFrom;
        }

        public String getPubKeyAlg() {
            return pubKeyAlg;
        }
    }

    /**
     * MagicXSign 자원 해제 수행
     */
    public void finish() {
        try {
            if (Xsign != null) {
                Xsign.Finish();
                Xsign = null;
            }
        } catch (MagicXSign_Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parameter가 없을 경우 기본으로 서명용 인증서 목록을 구성한다.
     *
     * @return
     * @throws MagicXSign_Exception
     */
    public ArrayList<CertDetailInfo> makeCertList() throws MagicXSign_Exception {
        return _makeCertList(MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
    }

    /**
     * nCertType 에 따른 인증서 목록을 구성한다.
     * nCertType 정보는 아래와 참조
     * MagicXSign_Type.XSIGN_PKI_CERT_SIGN : 서명용 인증서
     * MagicXSign_Type.XSIGN_PKI_CERT_KM   : 암호용 인증서
     *
     * @param nCertType
     * @return
     * @throws MagicXSign_Exception
     */
    public ArrayList<CertDetailInfo> makeCertList(int nCertType) throws MagicXSign_Exception {
        return _makeCertList(nCertType);
    }

    /**
     * 2020.05.28
     * XML 서명을 위한 인증서 목록을 구성한다.
     * 서명용 인증서만 하고 암호용이 필요하면 추가
     *
     * @return
     * @throws MagicXSign_Exception
     */
    public ArrayList<CertDetailInfo> makeXMLCertList() throws MagicXSign_Exception {
        return _makeXMLCertList(MagicXSign_Type.XSIGN_PKI_CERT_SIGN);
    }

    /**
     * MagicXSign을 이용하여 NPKI 인증서의 정보를 획득 및 ArrayList를 구성한다.
     * nCertType 정보는 아래와 참조
     * MagicXSign_Type.XSIGN_PKI_CERT_SIGN : 서명용 인증서
     * MagicXSign_Type.XSIGN_PKI_CERT_KM   : 암호용 인증서
     *
     * @param nCertType - 인증서 Type 정보
     * @return
     * @throws MagicXSign_Exception
     */
    private ArrayList<CertDetailInfo> _makeCertList(int nCertType) throws MagicXSign_Exception {
        ArrayList<CertDetailInfo> certListArray = null;

        try {
            CertDetailInfo certInfo;
            byte[] binTempCert;
            int nCount = 0;
            int nIndex = 0;

            // 인증서 목록 Memory 에 Load
            // "sdcard" -> mediaRootPath
            String mediaRootPath = Environment.getExternalStorageDirectory().getPath();

            if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_SIGN) {
                Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);
                nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            } else if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_KM) {
                Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);
                nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI);
            }

            if (nCount <= 0) {
                try {
                    /*
                     * 현재 저장되어 있는 인증서가 없을 경우, Sample Test를 위해
                     * Assets 에 있는 Sample 인증서를 저장 및 Load 한다.
                     * Sample 인증서의 비밀번호는 "qwer1234"
                     */
                    LoadSampleCert(nCertType);

                    if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_SIGN)
                        nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
                    else if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_KM)
                        nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI);

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new MagicXSign_Exception("Sample 인증서 Loading 오류, assets 폴더에 Sample 인증서 확인하세요");
                }
            }

            certListArray = new ArrayList<CertDetailInfo>();

            // 획득한 인증서 수 만큼 인증서정보를 획득하여  ArrayList<CertDetailInfo> 에 저장한다.
            // 더 자세한 정보 획득은 MagicXSign 사용 설명 참고
            for (nIndex = 0; nIndex < nCount; nIndex++) {
                binTempCert = Xsign.MEDIA_ReadCert(nIndex, nCertType, null);
                certInfo = new CertDetailInfo();

                // 인증서 index
                certInfo.index = nIndex;

                // 인증서 사용용도 설정
                certInfo.OID = Xsign.CERT_GetAttribute(binTempCert, MagicXSign_Type.XSIGN_CERT_ATTR_POLICY_ID, true);

                // 인증서 사용 목적
                certInfo.KeyUsage = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_KEY_USAGE, false);

                // 공개키 알고리즘 (20.05.28 : 추가)
                certInfo.pubKeyAlg = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_PUBLIC_KEY_ALGO, false);

                // 인증서 사용자 이름 설정
                certInfo.SubjectDN = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_SUBJECT_DN, false);

                // 인증서 유효기간 설정
                certInfo.ExpirationTo = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_EXPIRATION_TO, false);
                certInfo.ExpirationFrom = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_EXPIRATION_FROM, false);

                // 인증서 발급기관 설정
                certInfo.CA = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_ISSUER_DN, false);

                certListArray.add(certInfo);

            }

        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            // 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;
        }

        return certListArray;
    }

    /**
     * 2020.05.28
     * MagicXSign을 이용하여 저장소에 있는 인증서의 정보를 획득 및 ArrayList를 구성한다.
     * XML 서명용이기 때문에 OID 로 분기한다.
     * nCertType 정보는 아래와 참조
     * MagicXSign_Type.XSIGN_PKI_CERT_SIGN : 서명용 인증서
     * MagicXSign_Type.XSIGN_PKI_CERT_KM   : 암호용 인증서
     *
     * @param nCertType - 인증서 Type 정보
     * @return
     * @throws MagicXSign_Exception
     */
    private ArrayList<CertDetailInfo> _makeXMLCertList(int nCertType) throws MagicXSign_Exception {
        ArrayList<CertDetailInfo> certListArray = null;

        try {
            CertDetailInfo certInfo;
            byte[] binTempCert;
            int nCount = 0;
            int nIndex = 0;

            // 인증서 목록 Memory 에 Load
            // "sdcard" -> mediaRootPath
            String mediaRootPath = Environment.getExternalStorageDirectory().getPath();
            if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_SIGN) {
                Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);
            } else if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_KM) {
                Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, mediaRootPath);
            }
            nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);

            if (nCount <= 0) {
                throw new MagicXSign_Exception("인증서가 없습니다.");
            }

            certListArray = new ArrayList<CertDetailInfo>();

            // 획득한 인증서 수 만큼 인증서정보를 획득하여  ArrayList<CertDetailInfo> 에 저장한다.
            // 더 자세한 정보 획득은 MagicXSign 사용 설명 참고
            for (nIndex = 0; nIndex < nCount; nIndex++) {
                binTempCert = Xsign.MEDIA_ReadCert(nIndex, nCertType, null);
                certInfo = new CertDetailInfo();

                // 인증서 index
                certInfo.index = nIndex;

                // 인증서 사용용도 설정
                certInfo.OID = Xsign.CERT_GetAttribute(binTempCert, MagicXSign_Type.XSIGN_CERT_ATTR_POLICY_ID, true);

                // OID 필터링
                certInfo.OID = certInfo.OID.replaceAll(" ", ".");

                boolean isFiltered = false;
                String[] OIDArr = OID_FILTER.split(",");
                for (String oidRule : OIDArr) {
                    if (certInfo.OID.equals(oidRule)) {
                        isFiltered = true;
                        break;
                    }
                }

                if (!isFiltered) {
                    continue;
                }

                // 인증서 사용 목적
                certInfo.KeyUsage = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_KEY_USAGE, false);

                // 공개키 알고리즘
                certInfo.pubKeyAlg = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_PUBLIC_KEY_ALGO, false);

                // 인증서 사용자 이름 설정
                certInfo.SubjectDN = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_SUBJECT_DN, false);

                // 인증서 유효기간 설정
                certInfo.ExpirationTo = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_EXPIRATION_TO, false);
                certInfo.ExpirationFrom = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_EXPIRATION_FROM, false);

                // 인증서 발급기관 설정
                certInfo.CA = Xsign.CERT_GetAttribute(null, MagicXSign_Type.XSIGN_CERT_ATTR_ISSUER_DN, false);

                certListArray.add(certInfo);

            }

        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            // 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;
        }

        return certListArray;
    }


    /**
     * 테스트 인증서를 내장/외장 메모리에 저장 테스트
     * 내장/외장 메모리에 인증서가 없다면 이 함수를 수행한 후에 다른 함수를 수행 테스트해야 정상적으로 작동
     *
     * @param binCert
     * @param binPriKey
     */
    public void insertCert(int nCertType, byte[] binCert, byte[] binPriKey, int saveSpace) {
        try {
            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서와 개인키를 원하는 외장 디스크에 저장한다.
            //인증서를 넣은 후에는 Xsign.MEDIA_ReLoad()를 하거나, Xsign.MEDIA_UnLoad() -> Xsign.MEDIA_Load() 를 통해 인증서 목록을 재구성한다.
            Xsign.MEDIA_WriteCertAndPriKey(binCert, binPriKey, saveSpace);
        } catch (MagicXSign_Exception e) {
            try {
                //3. 외장 디스크에 저장에 실패했을 경우, 내장 디스크에 저장하도록 한다.
                Xsign.MEDIA_WriteCertAndPriKey(binCert, binPriKey, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            try {
                //4. 외장 디스크에 저장에 실패했을 경우, 내장 디스크에 저장하도록 한다.
                Xsign.MEDIA_WriteCertAndPriKey(binCert, binPriKey, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            //5. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;

        }
    }

    /**
     * 선택된 인증서를 이용하여 서명 데이타를 생성
     *
     * @param nCertIndex
     * @param paramPlainText
     * @param paramPassword
     * @return byte Array 서명값
     * @throws MagicXSign_Exception
     */
    public byte[] certSignData(int nCertIndex, String paramPlainText, String paramPassword) throws MagicXSign_Exception {
        byte[] binSignData = null;
        int nCertCount = 0;

        try {
            //1. 인증서 리스트를 구성한다.

            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 개수를 구한다.
            nCertCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCertCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            //3. 해당 인덱스의 인증서를 이용하여 서명한다.
            binSignData = Xsign.CMS_SignData(MagicXSign_Type.XSIGN_PKI_OPT_NONE, nCertIndex, paramPassword.getBytes(), paramPlainText.getBytes());

            //4. 서명된 데이터를 검증한다.
            byte[] binVerifyData = Xsign.CMS_VerifyData(MagicXSign_Type.XSIGN_PKI_OPT_NONE, binSignData);

            String s = XSignCertPolicy.ByteToHex(binSignData);

            // 서명 결과 디버그
            for (int idx = 0; idx < s.length(); idx++) {
                if (idx % 1000 == 0) {
                    System.out.println();
                }
            }

            //5. 원본 메세지와 같은 확인한다.
            if (paramPlainText.compareTo(new String(binVerifyData)) != 0)
                throw new MagicXSign_Exception("서명검증 실패", MagicXSign_Err.ERR_VERIFY_SIGNATURE);

        } catch (MagicXSign_Exception xSignException) {
            throw xSignException;
        } finally {
            //6. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (MagicXSign_Exception xSignException) {
                throw xSignException;
            }
            ;
        }
        return binSignData;
    }

    /**
     * XML 서명
     *
     * @param nCertIndex
     * @param paramPlainText
     * @param paramPassword
     * @return
     * @throws MagicXSign_Exception
     */
    public byte[] xmlCertSignData(int nCertIndex, String paramPlainText, String paramPassword) throws MagicXSign_Exception {
        byte[] binSignData;
        int nCertCount = 0;

        try {
            //1. 인증서 리스트를 구성한다.

            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 개수를 구한다.
            nCertCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCertCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            byte[] byCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, null);
            byte[] byPriKey = Xsign.MEDIA_ReadPriKey(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);

            // 서명 데이터 생성
            binSignData = Xsign.CRYPTO_SignData(byCert, byPriKey, paramPassword.getBytes(), paramPlainText.getBytes());

            String s = XSignCertPolicy.ByteToHex(binSignData);

            for (int idx = 0; idx < s.length(); idx++) {
                if (idx % 1000 == 0) {
                    System.out.println();
                }
            }

        } catch (MagicXSign_Exception xSignException) {
            throw xSignException;
        } finally {
            //6. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (MagicXSign_Exception xSignException) {
                throw xSignException;
            }
            ;
        }
        return binSignData;
    }

    /**
     * 20.04.22 : 현재 인덱스의 인증서 값을 얻어온다.
     *
     * @param nCertIndex
     * @return
     * @throws MagicXSign_Exception
     */
    public byte[] getCurrentCert(int nCertIndex) throws MagicXSign_Exception {

        int nCertCount = 0;
        byte[] cert = null;
        int nMediaType[] = new int[1];

        try {
            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 개수를 구한다.
            nCertCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCertCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            //3. 해당 인덱스의 인증서를 리턴한다.
            cert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);

        } catch (MagicXSign_Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Xsign.MEDIA_UnLoad();
            } catch (MagicXSign_Exception xSignException) {
                throw xSignException;
            }

        }

        return cert;
    }

    /**
     * SignedInfo 의 xml 값을 GPKI 형식으로 바꿔준다.
     *
     * @param signedInfoVal
     */
    public String convertSignedInfoGPKI(String signedInfoVal) {
        String ret = signedInfoVal;
        ret = ret.replace("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256", "http://www.tta.or.kr/2002/11/xmldsig#kcdsa-sha256");
        return ret;
    }

    /**
     * 선택된 인증서의 비밀번호를 변경 한다.(Sample 인증서 비밀번호는 qwer1234)
     *
     * @param nCertIndex
     * @param paramOldPassword
     * @param paramNewPassword
     * @return boolean
     * @throws MagicXSign_Exception
     */
    public boolean certChangePassword(int nCertIndex, String paramOldPassword, String paramNewPassword) throws MagicXSign_Exception {
        int nCertCount = 0;
        boolean bRet = false;
        int nMediaType[] = new int[1];

        try {
            Log.e("XSign", "[certChangePassword] nCertIndex: " + nCertIndex);
            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 갯수를 구한다.
            nCertCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCertCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            byte[] binCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);

            String subjectDN = Xsign.CERT_GetAttribute(binCert, MagicXSign_Type.XSIGN_CERT_ATTR_SUBJECT_DN, true);

            Log.e("XSign", "[certChangePassword] subjectDN: " + subjectDN);

            //3. 해당 인덱스의 인증서 개인키 비밀번호를 변경한다.
            bRet = Xsign.MEDIA_ChangePassword(nCertIndex, paramOldPassword.getBytes(), paramNewPassword.getBytes());
        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //5. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;
        }
        return bRet;
    }

    /**
     * 내장/외장 메모리에 저장되어 있는 인증서를 삭제 테스트
     *
     * @param nCertIndex
     * @return
     * @throws MagicXSign_Exception
     */
    public boolean deleteCert(int nCertIndex) throws MagicXSign_Exception {
        boolean bRet = false;
        try {
            int nCount = 0;

            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 갯수를 구한다.
            nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            //3. 인증서를 삭제한다.
            //인증서를 삭제 후에는 Xsign.MEDIA_ReLoad()를 하거나, Xsign.MEDIA_UnLoad() -> Xsign.MEDIA_Load() 를 통해 인증서 목록을 재구성한다.
            bRet = Xsign.MEDIA_DeleteCertificate(nCertIndex);
            Xsign.MEDIA_ReLoad();
        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //4. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();

                Log.e("XSign", "[DeleteCertificate] Xsign.MEDIA_GetCertCount(): " + Xsign.MEDIA_GetCertCount());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return bRet;
    }


    /**
     * SEED 알고리즘을 이용하여 암복호화 테스트
     * 출력 Buffer(outEncryptData and outDecryptData 가 Null 이 아니라면, Seed 로 암/복호화된 결과값을 넘겨준다.
     *
     * @throws MagicXSign_Exception
     */
    public boolean seedEncryptAndDecrypt(String paramPlainData, StringBuffer outEncryptData, StringBuffer outDecryptData) throws MagicXSign_Exception {
        byte[] binKey = null;
        byte[] binIV = null;
        byte[] binEncryptData = null;
        byte[] binDecryptData = null;
        boolean bRet = false;

        try {
            //1. SEED 알고리즘의 Key, IV 값을 생성한다.
            Xsign.CRYPTO_GenKeyAndIV("SEED");

            //2. 해당 Key값과 IV 값을 가져온다.
            binKey = Xsign.CRYPTO_GetKey();
            binIV = Xsign.CRYPTO_GetIV();

            //3. 설정된 Key, IV 값을 이용하여 대칭키 암호화를 한다.
            binEncryptData = Xsign.CRYPTO_Encrypt(paramPlainData.getBytes());

            //4. 2에서 가져온 Key, IV값을  설정한다.
            Xsign.CRYPTO_SetKeyAndIV("SEED", binKey, binIV);

            //5. 3에서 암호화한 메세지를 대칭키 복호화 한다.
            binDecryptData = Xsign.CRYPTO_Decrypt(binEncryptData);

            //6. 원본 메세지와 복화하한 메세지와 같은지 확인한다.
            if (paramPlainData.compareTo(new String(binDecryptData)) == 0) {
                // 암/복호 검증 결과
                bRet = true;
                // 출력 Buffer 가 Null 이 아니라면, Seed 로 암/복호화된 결과값을 넘겨준다.
                if (outEncryptData != null && outDecryptData != null) {
                    outEncryptData.append(XSignCertPolicy.ByteToHex(binEncryptData));
                    outDecryptData.append(new String(binDecryptData));
                }
            }

        } catch (MagicXSign_Exception e) {
            throw e;
        }
        return bRet;
    }

    /**
     * Hash 생성 수행
     *
     * @param paramPlainData
     * @return
     * @throws MagicXSign_Exception
     */
    public byte[] makeHash(String paramPlainData) throws MagicXSign_Exception {
        byte[] binHashData = null;
        try {
            // Hash를 생성한다.
            binHashData = Xsign.CRYPTO_Hash("SHA256", paramPlainData.getBytes());

        } catch (MagicXSign_Exception e) {
            throw e;
        }
        return binHashData;
    }

    /**
     * Base64 Encode 수행
     *
     * @param binData
     * @return
     * @throws MagicXSign_Exception
     */
    public String makeEncodeBase64(byte[] binData) throws MagicXSign_Exception {
        String sEncodedData = "";
        try {
            // BASE64 Encoding Message를 생성한다.
            sEncodedData = Xsign.BASE64_Encode(binData);

        } catch (MagicXSign_Exception e) {

            throw e;
        }

        return sEncodedData;
    }

    /**
     * Base64 Decode 수행
     *
     * @param paramData
     * @return
     * @throws MagicXSign_Exception
     */
    public byte[] makeDecodeBase64(String paramData) throws MagicXSign_Exception {
        byte[] binDecodedData = null;
        try {
            // BASE64 Encoding Message를 Decoding 한다.
            binDecodedData = Xsign.BASE64_Decode(paramData);

        } catch (MagicXSign_Exception e) {
            throw e;
        }

        return binDecodedData;
    }

    /**
     * 인증서에서 추출한 VID(주민번호) 검증 수행
     *
     * @param nCertIndex
     * @param paramIDN
     * @param paramPassword
     * @return VID 검증 결과(성공-true, 실패-false)
     * @throws MagicXSign_Exception
     */
    public boolean checkVID(int nCertIndex, String paramIDN, String paramPassword) throws MagicXSign_Exception {
        byte[] binCert = null;
        byte[] binKey = null;
        int nCount = 0;
        int nMediaType[] = new int[1];
        boolean bRet = false;

        try {
            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서의 갯수를 가져온다.
            nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCount <= 0)
                throw new MagicXSign_Exception("No Certificate List Item");

            //3. 해당 인덱스의 인증서 binary를 가져온다.
            binCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);

            //4. 해당 인덱스의 인증서 개인키 binary를 가져온다.
            binKey = Xsign.MEDIA_ReadPriKey(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);

            //5. VID 검증을 한다.
//    		bRet = Xsign.VID_Verify(binCert, binKey, paramPassword, paramIDN);

            // String 형의 경우 메모리에 남기 때문에 BTE[] 형을 사용해야 한다
            bRet = Xsign.VID_Verify(binCert, binKey, paramPassword.getBytes(), paramIDN.getBytes());

        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //6. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;
        }
        return bRet;
    }

    /**
     * 인증서를 이용 하여 서명 데이터 생성하고, 생성한 서명 데이타를 검증 수행
     *
     * @param nCertIndex
     * @param paramPassword
     * @param paramPlainData
     * @return 서명 데이터 검증결과(성공-true, 실패-false)
     * @throws MagicXSign_Exception
     */
    public byte[] cryptoSignVerify(int nCertIndex, String paramPlainData, String paramPassword) throws MagicXSign_Exception {
        byte[] binCert = null;
        byte[] binKey = null;
        byte[] binSignature = null;
        boolean bRet = false;
        int nCertCount = 0;

        try {

            //1. 인증서 리스트를 구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_REMOVABLE, "/sdcard");

            //2. 인증서 갯수를 구한다.
            nCertCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCertCount <= 0)
                throw new MagicXSign_Exception("No Certificate List Item");

            //3. 해당 인덱스의 인증서 binary를 구한다.
            binCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, null);

            //4. 해당 인덱스의 개인키 binary를 구한다.
            binKey = Xsign.MEDIA_ReadPriKey(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);

            //5. CRYPTO 서명을 한다.
            binSignature = Xsign.CRYPTO_SignData(binCert, binKey, paramPassword, paramPlainData.getBytes());

            //6. CRYPTO 서명 검증을 한다.
            bRet = Xsign.CRYPTO_VerifyData(binCert, binKey, paramPassword, paramPlainData.getBytes(), binSignature);
            if (bRet == false)
                throw new MagicXSign_Exception("서명검증 실패", MagicXSign_Err.ERR_VERIFY_SIGNATURE);

        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //7. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
        }
        return binSignature;
    }

    /**
     * 서명용 인증서 이용한 비대칭키 암/복호화 테스트
     * 출력 Buffer(outEncryptData and outDecryptData 가 Null 이 아니라면
     * 비대칭키 암/복호화된 결과값을 넘겨준다.
     *
     * @param nCertIndex
     * @param paramPlainData
     * @param paramPassword
     * @param outEncryptData
     * @param outDecryptData
     * @return
     * @throws MagicXSign_Exception
     */
    public boolean AsymcEncryptAndDecrypt(int nCertIndex, String paramPlainData, String paramPassword,
                                          StringBuffer outEncryptData, StringBuffer outDecryptData) throws MagicXSign_Exception {

        int nCount = 0;
        int nMediaType[] = new int[1];
        boolean bRet = false;

        try {
            //1. 인증서 리스트를  구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 갯수를 구성한다.
            nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI);
            if (nCount < 0)
                throw new MagicXSign_Exception("No Certificate List Item");

            //3. 해당 인덱스의 인증서 binary를 가져온다.
            byte[] binCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, nMediaType);

            //4. 해당 인덱스의 인증서 개인키 binary를 가져온다.
            byte[] binKey = Xsign.MEDIA_ReadPriKey(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_SIGN);

            //5. 해당 인증서와 개인키를 이용하여 비대칭키 암호화를 한다.
            byte[] binEncData = Xsign.CRYPTO_AsymEncrypt(binCert, false, (byte[]) null, paramPlainData.getBytes());

            //6. 해당 인증서와 개인키를 이용하여 비대칭키 복호화를 한다.
            byte[] binDecData = Xsign.CRYPTO_AsymDecrypt(binKey, true, paramPassword, binEncData);

            //7. 원본 메시지와 복화하한 메세지와 같은지 확인한다.
            if (paramPlainData.compareTo(new String(binDecData)) == 0) {
                bRet = true;
                // 출력 Buffer 가 Null 이 아니라면, 비대칭키 암/복호화된 결과값을 넘겨준다.
                if (outEncryptData != null && outDecryptData != null) {
                    outEncryptData.append(XSignCertPolicy.ByteToHex(binEncData));
                    outDecryptData.append(new String(binDecData));
                }
            }

        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //8. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (Exception e) {
            }
            ;
        }

        return bRet;
    }

    /**
     * 인증서 이용하여 서명 후 Evelope Data 암/복호 수행
     * 출력 Buffer(outEncryptData and outDecryptData 가 Null 이 아니라면
     * 암/복호화된 결과값을 넘겨준다.
     *
     * @param nCertIndex
     * @param paramPlainData
     * @param paramPassword
     * @param outEncryptData
     * @param outDecryptData
     * @return
     * @throws MagicXSign_Exception
     */
    public boolean envelopedEncryptAndDecrypt(int nCertIndex, String paramPlainData, String paramPassword,
                                              StringBuffer outEncryptData, StringBuffer outDecryptData) throws MagicXSign_Exception {
        boolean bRet = false;
        int nCount = 0;
        int nMediaType[] = new int[1];

        try {
            //1. 암호용 인증서 리스트를  구성한다.
            Xsign.MEDIA_Load(iPkiType, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_KM, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");

            //2. 인증서 갯수를 구성한다.
            nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI);
            if (nCount <= 0) {
                /*
                 * 만약 NPKI Type에 암호 인증서가 존재하지 않는다면
                 * 암호용 Sample 인증서가 저장 되어 있는지 검사하기 위해 PPKI 를 검사한다.
                 */
                Xsign.MEDIA_UnLoad();
                Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_KM, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
                nCount = Xsign.MEDIA_GetCertCount(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI);
                if (nCount <= 0)
                    throw new MagicXSign_Exception("No Certificate List Item");
            }

            //3. 해당 인덱스의 인증서 binary를 가져온다.
            byte[] binCert = Xsign.MEDIA_ReadCert(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_KM, nMediaType);

            //4. 해당 인덱스의 인증서 개인키 binary를 가져온다.
            byte[] binKey = Xsign.MEDIA_ReadPriKey(nCertIndex, MagicXSign_Type.XSIGN_PKI_CERT_KM);

            //5. 해당 인증서와 개인키를 이용하여 CMS 서명 한다.
            byte[] binEncData = Xsign.CMS_EncryptData(MagicXSign_Type.XSIGN_PKI_OPT_USE_CONTENT_INFO, "SEED", binCert, paramPlainData.getBytes());

            //6. 해당 인증서와 개인키를 이용하여 CMS 서명한 message를 검증한다.
            byte[] binDecData = Xsign.CMS_DecryptData(binCert, binKey, paramPassword, binEncData);

            //7. 원본 message와 검증 된 message를 비교한다.
            if (paramPlainData.compareTo(new String(binDecData)) == 0) {
                bRet = true;
                // 출력 Buffer 가 Null 이 아니라면, 비대칭키 암/복호화된 결과값을 넘겨준다.
                if (outEncryptData != null && outDecryptData != null) {
                    outEncryptData.append(XSignCertPolicy.ByteToHex(binEncData));
                    outDecryptData.append(new String(binDecData));
                }
            }
        } catch (MagicXSign_Exception e) {
            throw e;
        } finally {
            //8. 인증서 리스트를 해제한다.
            try {
                Xsign.MEDIA_UnLoad();
            } catch (MagicXSign_Exception e) {
                throw e;
            }
            ;
        }
        return bRet;
    }

    public byte[] makeUcpidReqInfo(int nCertIndex, String strPwd, String userAgreement, boolean bRealName, boolean bGender, boolean bNationalInfo, boolean bBirthDate, boolean bCi) throws MagicXSign_Exception {
        byte[] binUcpidReqInfo = null;
        byte[] binSignedUcpidReqInfo = null;
        byte[] nonce = new byte[16];
        int nCertCount = 0;

        SecureRandom rand = new SecureRandom();
        rand.nextBytes(nonce);

        try {
            Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, MagicXSign_Type.XSIGN_PKI_CERT_SIGN, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
            nCertCount = Xsign.MEDIA_GetCertCount();

            if (nCertCount <= 0)
                throw new MagicXSign_Exception("인증서 존재하지 않음", MagicXSign_Err.ERR_READ_CERT);

            binUcpidReqInfo = Xsign.CMS_MakeUCPIDRequestInfo(userAgreement,
                    bRealName,
                    bGender,
                    bNationalInfo,
                    bBirthDate,
                    bCi,
                    nonce,
                    "www.dreamsecurity.com");

            binSignedUcpidReqInfo = Xsign.CMS_SignData(MagicXSign_Type.XSIGN_PKI_OPT_NONE,
                    nCertIndex,
                    strPwd,
                    binUcpidReqInfo);

        } catch (MagicXSign_Exception e) {
            throw e;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } finally {
            try {
                Xsign.MEDIA_UnLoad();
            } catch (MagicXSign_Exception e) {
                throw e;
            }
            ;
        }

        return binSignedUcpidReqInfo;
    }

    /**
     * assets 폴더에 있는 Sample 인증서를 byte Array 형태로 Load
     *
     * @param Certname
     * @return
     * @throws Exception
     */
    private byte[] getCertByteArray(String Certname) throws Exception {
        InputStream in;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        // ASSETS에 존재하는 인증서 파일을 읽어온다.
        in = mContext.getAssets().open(Certname);

        int nRead;
        byte[] data = new byte[1024];
        // ASSETS에 존재하는 DSTK 환경설정 파일을 byte[] 로 읽어온다.
        while ((nRead = in.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();

        return buffer.toByteArray();
    }

    /**
     * MagicXSign Sample에서 사용 할 인증서를 멤버변수에 Load 후 Save
     *
     * @param nCertType
     * @throws Exception
     */
    private void LoadSampleCert(int nCertType) throws Exception {
        // 210317 wj : 국민은행
        int SDK_INT = Build.VERSION.SDK_INT;
        int saveSpace = MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK;

        if (SDK_INT >= 30)
            saveSpace = MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_DISK;
        else
            saveSpace = MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_REMOVABLE;
        /*
         * 서명용 Sample 인증서 저장 및 Load
         */
        if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_SIGN) {
            // 홍길동 서명용 인증서
            mbinSampleSignCert = getCertByteArray("signCert.der");
            mbinSampleSignPri = getCertByteArray("signPri.key");
            insertCert(nCertType, mbinSampleSignCert, mbinSampleSignPri, saveSpace);

            // 전자세금용(MagicXSignXML) 서명용 인증서
            mbinSampleSignCert = getCertByteArray("cn=전자세금용001()00996802021031000007,ou=TEST,ou=KFTC,ou=xUse4Esero,o=yessign,c=kr/signCert.der");
            mbinSampleSignPri = getCertByteArray("cn=전자세금용001()00996802021031000007,ou=TEST,ou=KFTC,ou=xUse4Esero,o=yessign,c=kr/signPri.key");
            insertCert(nCertType, mbinSampleSignCert, mbinSampleSignPri, saveSpace);

            Xsign.MEDIA_UnLoad();
            Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_NPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
        }
        /*
         * 암호용 Sample 인증서 저장 및 Load
         */
        else if (nCertType == MagicXSign_Type.XSIGN_PKI_CERT_KM) {
            mbinSampleKmCert = getCertByteArray("kmCert.der");
            mbinSampleKmPri = getCertByteArray("kmPri.key");
            insertCert(nCertType, mbinSampleKmCert, mbinSampleKmPri, saveSpace);
            Xsign.MEDIA_UnLoad();
            Xsign.MEDIA_Load(MagicXSign_Type.XSIGN_PKI_TYPE_PPKI, MagicXSign_Type.XSIGN_PKI_CERT_TYPE_USER, nCertType, MagicXSign_Type.XSIGN_PKI_MEDIA_TYPE_ALL, "/sdcard");
        }
    }

    /**
     * MagicXSign Sample에서 사용 할 RootCA 인증서 Load
     *
     * @throws Exception
     */
    private void LoadSampleCACert() throws Exception {
        mbinCACert = getCertByteArray("caCert.der");
        mbinRootCACert = getCertByteArray("rootCert.der");
    }


}
