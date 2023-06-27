package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.DTO.DtoPostUser;
import com.dki.hybridapptest.DTO.DtoRetrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<DtoRetrofit> getOneUserInfo(@Path("id") String id);

    @POST("users/")
    Call<DtoPostUser> getUserInfo(@Body DtoPostUser dtoPostUser);
}