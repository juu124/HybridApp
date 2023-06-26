package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.DTO.DTOPostResult;
import com.dki.hybridapptest.DTO.DTORetrofit;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<DTORetrofit> Repos(@Path("id") String id);

    @POST("users/")
    Call<DTORetrofit> callBody(@Body DTOPostResult dtoPostResult);
}