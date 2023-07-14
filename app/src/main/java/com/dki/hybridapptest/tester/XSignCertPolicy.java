package com.dki.hybridapptest.tester;

import com.dreamsecurity.magicxsign.MagicXSign_Type;

public class XSignCertPolicy {

    // 인증서 OID 값
    private static final String[] OID = {
            ("1 2 410 100001 2 1 1"),
            ("1 2 410 100001 2 1 2"),
            ("1 2 410 100001 2 1 3"),
            ("1 2 410 100001 2 2 1"),
            ("1 2 410 200004 5 1 1 1"),
            ("1 2 410 200004 5 1 1 2"),
            ("1 2 410 200004 5 1 1 3"),
            ("1 2 410 200004 5 1 1 4"),
            ("1 2 410 200004 5 1 1 5"),
            ("1 2 410 200004 5 1 1 6"),
            ("1 2 410 200004 5 1 1 7"),
            ("1 2 410 200004 5 1 1 8"),
            ("1 2 410 200004 5 1 1 9"),
            ("1 2 410 200004 5 1 1 9 2"),
            ("1 2 410 200004 5 1 1 10"),
            ("1 2 410 200004 5 1 1 11"),
            ("1 2 410 200004 5 1 1 12"),
            ("1 2 410 200004 5 2 1 1"),
            ("1 2 410 200004 5 2 1 2"),
            ("1 2 410 200004 5 2 1 3"),
            ("1 2 410 200004 5 2 1 4"),
            ("1 2 410 200004 5 2 1 5"),
            ("1 2 410 200004 5 2 1 7 1"),
            ("1 2 410 200004 5 2 1 7 2"),
            ("1 2 410 200004 5 2 1 7 3"),
            ("1 2 410 200004 5 3 1 1"),
            ("1 2 410 200004 5 3 1 2"),
            ("1 2 410 200004 5 3 1 3"),
            ("1 2 410 200004 5 3 1 5"),
            ("1 2 410 200004 5 3 1 6"),
            ("1 2 410 200004 5 3 1 7"),
            ("1 2 410 200004 5 3 1 8"),
            ("1 2 410 200004 5 3 1 9"),
            ("1 2 410 200004 5 4 1 1"),
            ("1 2 410 200004 5 4 1 2"),
            ("1 2 410 200004 5 4 1 3"),
            ("1 2 410 200004 5 4 1 4"),
            ("1 2 410 200004 5 4 1 5"),
            ("1 2 410 200004 5 4 1 101"),
            ("1 2 410 200004 5 4 1 102"),
            ("1 2 410 200004 5 4 1 103"),
            ("1 2 410 200004 5 4 1 104"),
            ("1 2 410 200005 1 1 1"),
            ("1 2 410 200005 1 1 2"),
            ("1 2 410 200005 1 1 4"),
            ("1 2 410 200005 1 1 5"),
            ("1 2 410 200005 1 1 6"),
            ("1 2 410 200005 1 1 6 1"),
            ("1 2 410 200005 1 1 6 2"),
            ("1 2 410 200004 2 1"),
            ("1 2 410 200012 1 1 1"),
            ("1 2 410 200012 1 1 2"),
            ("1 2 410 200012 1 1 3"),
            ("1 2 410 200012 1 1 4"),
            ("1 2 410 200012 1 1 5"),
            ("1 2 410 200012 1 1 6"),
            ("1 2 410 200012 1 1 7"),
            ("1 2 410 200012 1 1 8"),
            ("1 2 410 200012 1 1 9"),
            ("1 2 410 200012 1 1 10"),
            ("1 2 410 200012 1 1 11"),
            ("1 2 410 200012 1 1 12"),
            ("1 2 410 200012 1 1 101"),
            ("1 2 410 200012 1 1 103"),
            ("1 2 410 200012 1 1 105"),
            ("1 2 410 100001 5 3 1 3")
    };

    // 인증서 OID에 따른 인증서 용도 String 값
    private static final String[] OIDString =
            {
                    ("전자관인"),
                    ("컴퓨터용"),
                    ("전자특수관인"),
                    ("공무원용"),
                    ("스페셜 개인"),
                    ("스페셜 개인서버"),
                    ("스페셜 법인"),
                    ("스페셜 서버"),
                    ("범용개인"),
                    ("범용개인서버"),
                    ("범용기업"),
                    ("범용서버"),
                    ("증권/보험용"),
                    ("신용카드용"),
                    ("골드 개인서버"),
                    ("실버 개인"),
                    ("실버 법인"),
                    ("범용기업"),
                    ("범용개인"),
                    ("특별등급(전자입찰)"),
                    ("1등급인증서(서버)"),
                    ("특별등급 법인"),
                    ("은행개인"),
                    ("증권/보험용"),
                    ("신용카드용"),
                    ("1등급(기관/단체)"),
                    ("범용기업"),
                    ("1등급(서버)"),
                    ("특수목적용(기관/단체)"),
                    ("특수목적용(법인)"),
                    ("특수목적용(서버)"),
                    ("특수목적용(개인)"),
                    ("범용개인"),
                    ("범용개인"),
                    ("범용기업"),
                    ("범용(서버)"),
                    ("특수목적용(개인)"),
                    ("특수목적용(법인)"),
                    ("은행개인"),
                    ("증권거래용"),
                    ("신용카드용"),
                    ("전자민원용"),
                    ("범용개인"),
                    ("은행기업"),
                    ("은행개인"),
                    ("범용기업"),
                    ("용도제한용"),
                    ("기업뱅킹용"),
                    ("신용카드용"),
                    ("공인인증기관"),
                    ("전자거래 서명용(개인)"),
                    ("전자거래 암호용(개인)"),
                    ("전자거래 서명용(법인)"),
                    ("전자거래 암호용(법인)"),
                    ("전자거래 서명용(서버)"),
                    ("전자거래 암호용(서버)"),
                    ("전자무역 서명용(개인)"),
                    ("전자무역 암호용(개인)"),
                    ("전자무역 서명용(법인)"),
                    ("전자무역 암호용(법인)"),
                    ("전자무역 서명용(서버)"),
                    ("전자무역 암호용(서버)"),
                    ("은행/보험용"),
                    ("증권/보험용"),
                    ("신용카드용"),
                    ("교육공무원용")
            };

    private static final String[] ISSUER =
            {
                    ("CA131000002"),
                    ("CA128000002"),
                    ("CA130000002"),
                    ("CA129000001"),
                    ("CA131000001"),
                    ("CA131000005"),
                    ("CA131000009"),
                    ("CA131000010"),
                    ("CA134040001"),
                    ("CA974000001"),
            };

    // 인증서 OID에 따른 인증서 용도 String 값
    private static final String[] ISSUERString =
            {
                    ("행정안전부"),
                    ("대검찰청"),
                    ("병무청"),
                    ("국방부"),
                    ("행정안전부"),
                    ("행정안전부"),
                    ("행정안전부"),
                    ("행정안전부"),
                    ("행정안전부"),
                    ("교육과학기술부"),
                    ("대법원"),
            };


    /**
     * 인증서의 OID 값을 파싱하여 인증서 용도를 획득
     */
    public static String parseOID(String paramOID) {
        int nIndex = 0;

        for (nIndex = 0; nIndex < OID.length; nIndex++) {
            if (OID[nIndex].compareTo(paramOID) == 0)
                return OIDString[nIndex];
        }

        return paramOID;
    }

    /**
     * 인증서 사용 목적 리턴
     *
     * @param paramKeyusage
     * @return
     */
    public static int getKeyPurpose(String paramKeyusage) {
        int nCertPurpose = -1;

        if (paramKeyusage.toLowerCase().contains("signature"))
            nCertPurpose = MagicXSign_Type.XSIGN_PKI_CERT_SIGN;
        else if (paramKeyusage.toLowerCase().contains("encipherment"))
            nCertPurpose = MagicXSign_Type.XSIGN_PKI_CERT_KM;

        return nCertPurpose;
    }

    /**
     * 인증서 사용 목적 String 으로 리턴
     *
     * @param paramKeyusage
     * @return
     */
    public static String getKeyPurposeForString(String paramKeyusage) {
        String szPurpose = "";
        int nKeyusage = getKeyPurpose(paramKeyusage);

        if (nKeyusage == MagicXSign_Type.XSIGN_PKI_CERT_SIGN)
            szPurpose = "서명용 인증서";
        else if (nKeyusage == MagicXSign_Type.XSIGN_PKI_CERT_KM)
            szPurpose = "암호용 인증서";

        return szPurpose;
    }

    /**
     * 인증서의 issuerdn 값을 파싱하여 인증서 발급기관을 획득
     */
    public static String parseISSUER(String paramIssur) {
        int nIndex = 0;

        String issurCA = GetDataFormString("cn=", paramIssur);

        for (nIndex = 0; nIndex < ISSUER.length; nIndex++) {
            if (ISSUER[nIndex].compareTo(issurCA) == 0)
                return ISSUERString[nIndex];
        }

        return issurCA;
    }

    /**
     * 인증서 발급 기관(CA) 을 획득
     */
    public static String GetDataFormString(String Type, String DN) {
        String O = null;
        int nStartO = -1, nEndO = -1;

        nStartO = DN.indexOf(Type);
        if (nStartO >= 0)
            nStartO += Type.length();

        nEndO = DN.indexOf(",", nStartO);

        if (nEndO > 0)
            O = DN.substring(nStartO, nEndO);
        else
            O = DN.substring(nStartO);

        return O;
    }

    /**
     * DN 정보에서 사용자 이름을 Parsing
     *
     * @param paramDN
     * @return
     */
    public static String parserUserName(String paramDN) {
        if (paramDN.indexOf("cn=") > -1) {
            int index1 = paramDN.indexOf("cn=");
            int index2 = "cn=".length();
            int index3 = paramDN.indexOf(",", index1);
            if (index3 == -1)
                index3 = paramDN.indexOf("(", index1);

            return paramDN.substring(index1 + index2, index3);
        } else
            return paramDN;
    }

    /**
     * byte 데이터를 Hex 로 변환
     *
     * @param raw
     * @return
     */
    public static String ByteToHex(byte[] binData) {
        String HEXES = "0123456789ABCDEF";
        StringBuilder hex;

        if (binData == null || binData.length <= 0)
            return "";

        hex = new StringBuilder(2 * binData.length);

        for (final byte b : binData) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
            hex.append(" ");
        }

        return hex.toString();
    }
}
