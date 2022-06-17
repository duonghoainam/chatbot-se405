package com.example.chatapp.Api;

import com.example.chatapp.Model.BotReply;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    //link API: http://localhost:5000/
    //http://192.168.2.80:5000
    //http://127.0.0.1:5000
    //https://jsonplaceholder.typicode.com/posts
    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    ApiService apiService = new Retrofit.Builder().
            baseUrl("http://localhost:5000")
            .addConverterFactory(GsonConverterFactory.create(gson)).
            build().
           create(ApiService.class);

    @POST("/")
    Call<BotReply> sendPosts(@Body BotReply botReply);
}
