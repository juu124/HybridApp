package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.dto.UserCreate;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UsersDetail;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<UsersDetail> getOneUserInfo(@Path("id") String id);

    @GET("users/{id}")
    Call<UserDataSupport> getOneUser(@Path("id") String id);

    @GET("users/")
    Call<UsersDetail> getUserInfoList();

    @GET("users/")
    Call<UsersDetail> getUserNextInfo(@Query("page") int page);

    @POST("users/")
    Call<UserCreate> getUserInfo(@Body UserCreate userCreate);
}