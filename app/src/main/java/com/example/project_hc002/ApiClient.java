package com.example.project_hc002;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import java.util.concurrent.TimeUnit;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.Request;
import com.google.gson.GsonBuilder;

public class ApiClient {
    private static final String BASE_URL = "http://192.168.2.212:3000/api/";
    //    private static final String BASE_URL = "http://192.168.69.250:3000/api/"; // Pastikan format URL benar
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Accept-Charset", "utf-8")
                                .header("Content-Type", "application/json; charset=utf-8")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                            .setLenient()
                            .create()))
                    .build();
        }
        return retrofit;
    }
}