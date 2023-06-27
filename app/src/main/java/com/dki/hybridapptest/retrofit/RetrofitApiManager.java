package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.dto.DtoJson;
import com.dki.hybridapptest.dto.DtoPostUser;
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

    public static Retrofit Build() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        return new Retrofit.Builder()
                .client(getUnsafeOkHttpClient().build()) //OkHttp 사용해서 로그 보기
                .baseUrl("https://reqres.in/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public void requestUserInfoList(RetrofitInterface retrofitInterface) {
        Build().create(RetrofitApiService.class).getUserInfoList().enqueue(new Callback<DtoJson>() {
            @Override
            public void onResponse(Call<DtoJson> call, Response<DtoJson> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<DtoJson> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    public void requestGetUser(RetrofitInterface retrofitInterface) {
        Build().create(RetrofitApiService.class).getOneUserInfo("2").enqueue(new Callback<DtoJson>() {
            @Override
            public void onResponse(Call<DtoJson> call, Response<DtoJson> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<DtoJson> call, Throwable t) {
                retrofitInterface.onFailure(t);
            }
        });
    }

    public void requestPostUser(RetrofitInterface retrofitInterface) {
        DtoPostUser dtoPostUser = new DtoPostUser("aaa", "leader");
        Build().create(RetrofitApiService.class).getUserInfo(dtoPostUser).enqueue(new Callback<DtoPostUser>() {
            @Override
            public void onResponse(Call<DtoPostUser> call, Response<DtoPostUser> response) {
                retrofitInterface.onResponse(response);
            }

            @Override
            public void onFailure(Call<DtoPostUser> call, Throwable t) {
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
