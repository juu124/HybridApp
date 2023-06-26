package com.dki.hybridapptest.retrofit;

import com.dki.hybridapptest.DTO.DTORetrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitApiService {
    @GET("users/{id}")
    Call<DTORetrofit> listRepos(@Path("id") String id);
}