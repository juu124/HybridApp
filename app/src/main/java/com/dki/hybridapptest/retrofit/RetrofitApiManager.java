package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.dto.LoginResponse;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UserSimpleAccount;
import com.dki.hybridapptest.dto.UsersList;
import com.dki.hybridapptest.utils.Constant;
import com.dki.hybridapptest.utils.GLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitApiManager {
    private static final long RETROFIT_TIME_OUT = 10;
    private static RetrofitApiManager instance;

    public static RetrofitApiManager getInstance() {
        if (instance == null) {
            instance = new RetrofitApiManager();
        }
        return instance;
    }

    // 유저 정보 목록 빌드
    public static Retrofit Build() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .client(getUnsafeOkHttpClient().build()) //OkHttp 사용해서 로그 보기
                .baseUrl(Constant.USERS_INFO_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    // 로그인
    public static Retrofit BuildLogin() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .client(getUnsafeOkHttpClient().build()) //OkHttp 사용해서 로그 보기
                .baseUrl(Constant.USERS_LOGIN_CHECK_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    // 유저 정보 요청
    public void requestOneUserInfo(String idNum, RetrofitInterface retrofitInterface) {
        Build().create(RetrofitApiService.class).getOneUser(idNum).enqueue(new Callback<UserDataSupport>() {
            @Override
            public void onResponse(Call<UserDataSupport> call, Response<UserDataSupport> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<UserDataSupport> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    // 유저 정보 목록 요청
    public void requestUserInfoList(RetrofitInterface retrofitInterface) {
        Build().create(RetrofitApiService.class).getUserInfoList().enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<UsersList> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    // 더보기 유저 정보 목록 요청
    public void requestUserNextList(int page, RetrofitInterface retrofitInterface) {
        Build().create(RetrofitApiService.class).getUserNextInfo(page).enqueue(new Callback<UsersList>() {
            @Override
            public void onResponse(Call<UsersList> call, Response<UsersList> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<UsersList> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    // 로그인 id, pw 확인
    public void requestPostLogin(String id, String pwd, RetrofitInterface retrofitInterface) {
        UserSimpleAccount userSimpleAccount = new UserSimpleAccount(id, pwd);
        BuildLogin().create(RetrofitApiService.class).getLoginInfo(userSimpleAccount).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    public static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor(); //OkHttp 사용해서 로그 보기
            RetrofitLogInterceptor logging = new RetrofitLogInterceptor(new RetrofitLogInterceptor.Logger() {

                @Override
                public void log(String message) {
                    if (GLog.isRetrofitLog) {
                        try {
                            // JSON Format이 아닌경우 Exception
                            new JSONObject(message);
                            GLog.d("RESPONSE JSON ->\n" + GLog.getPretty(message));
                        } catch (JSONException e) {
                            GLog.d(message);
                        }
                    }
                }
            });
            logging.setLevel(RetrofitLogInterceptor.Level.BODY);
            builder.addInterceptor(logging);

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            //20200602 - 네트워크 연결 상태 TimeOut 추가 - 15초? 30초?
            builder.connectTimeout(RETROFIT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(RETROFIT_TIME_OUT, TimeUnit.SECONDS);
            builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));

            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
