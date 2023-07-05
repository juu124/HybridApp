package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.dto.UserCreate;
import com.dki.hybridapptest.dto.UserCreateResponse;
import com.dki.hybridapptest.dto.UserDataSupport;
import com.dki.hybridapptest.dto.UsersList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<UserDataSupport> getOneUser(@Path("id") String id);

    @GET("users/")
    Call<UsersList> getUserInfoList();

    @GET("users/")
    Call<UsersList> getUserNextInfo(@Query("page") int page);

    @POST("users/")
    Call<UserCreate> getUserInfo(@Body UserCreateResponse userCreateResponse);
}