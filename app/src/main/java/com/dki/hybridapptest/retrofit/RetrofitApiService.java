package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.dto.DtoJson;
import com.dki.hybridapptest.dto.DtoPostUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<DtoJson> getOneUserInfo(@Path("id") String id);

    @GET("users/")
    Call<DtoJson> getUserInfoList();

    @POST("users/")
    Call<DtoPostUser> getUserInfo(@Body DtoPostUser dtoPostUser);
}