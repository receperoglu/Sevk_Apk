package com.eroglu.sevk.network;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.eroglu.sevk.DomainName.getRawURL;


public class ServiceGenerator {

    public static FileUploadService createService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES).addInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder requestBuilder = requestBuilder = original.newBuilder()
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }).build();


        return new Retrofit
                .Builder()
                .baseUrl(getRawURL())
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(FileUploadService.class);
    }
}
