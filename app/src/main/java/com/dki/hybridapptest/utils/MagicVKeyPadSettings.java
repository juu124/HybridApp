package com.dki.hybridapptest.utils;

import com.dreamsecurity.magicvkeypad.MagicVKeypadType;

public class MagicVKeyPadSettings {

    //보안키패드 라이선스
    // 신정원 kcredit-mydatacenter-aos 보안키패드 라이선스
    public static String strLicense = "J7xMz5SURoVs9+DU8xXy9QwY2mqjI2zwldVE2ZddtuiJumBZpEwnezvLlNbitffIKmGUZiCFfwg16Jk2gRxSHauHuHzxwAvzhjQzjfeQpR+Llh60wE9/8SVvO0IRWbl+UWFYhJn0A9/X8TCVTjxa7+OFcOGCi2vKyRmX2p2WXuBJcIVgBLp6VN0m6Ez5gW/g2IutAUriF+/9uf8RACtsxcfXJsPZ8X50mMNirdfog8Vd9LtebVgzVP3E45xK2XbMvexiKvxmlO23sCamRgtdlu9zFXf8qi5YpmJOUl51f8debydvcxaJJC23ToWXLlbLTM2NUvA+zs7Xic6yT0UWKw==";

    // MagicVKeypadSample 보안키패드 라이선스
//    public static String strLicense = "mThjeTgwnJw20h0RKNbtpeiQ8nF47Z5IHyXnLAvUu5fXg4b5Mgf1FB3Jt9AG4fbtR6RCIj39/Fbctw6mwTIpz9IIUB57zp//LrYZG3sv4tuEzW7w3EE8f5/qJWBNB/Tp5eygfMn2a0MO732rtWRh9jknu+KWstyQ4Dv6hplbXWZxwZzg4jQg9JVKNuL8bASsWlqwvdQTihqc5zUDqRsqurWf68aY/+GM3ylG1ilNH0wRlSLiDm/Qm7uGgSme39z118YEi6DLT+W488GJHPzmPCl2pTGq3GtfAFrjLWHZWopDoWiESbHXRVUzXoDQw3y5VqRbxlo8O2Gh+K1lOp5ZDw==";

    //공개키 스트링
    public static String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh1VcmGrI1cd76W20wkbcvjgc8KDsZshJDLAL7iY9wuGpWi8OGij6PlYc/XuGJptMr9/8E3B0BIps/YeQixqU6sAmxUbImws0FiIIGW0hHlXC3AmjghVm35Ygttwr40tL/5zqIijy+1bS7u0J7J37NyzkC1NzoeMpOjNHk2t+T0beFAy03O4NBEgO8utbR04q9M3Mmigkgu2ljpsnAPgSHgtlA0OLw5tHvoUs5WjsTqNTPmz4Dww7AtGdn5sVBfDAXp5R009LwJw31/xsyrq4jATI5eNYEjIBtk+cqwRoIhoSPW/HBgeFpv801uQaTwxuO3Gs8XH8ir4srYcwA4Q0MQIDAQAB";
    public static boolean bUseE2E = false;
    public static boolean bIsFullMode = true;
    public static boolean bIsPortFix = false;
    public static int maskingType = MagicVKeypadType.MAGICVKEYPAD_TYPE_DEFAULTMASKING;
    public static boolean bAllowCapture = false;
    public static int maxLength = 120;
    public static boolean bMultiClick = false;
    public static boolean bUseDummydata = false;
    public static boolean bUseReplace = true;

    public static String charFieldName = "insertChar";
    public static String numFieldName = "insertNum";
    public static boolean bUseSpeaker = false;

    public static String E2ETestUrl = "http://10.10.30.130:8080/MagicKeypadServer/decryptKeypadRecord.jsp";


}
