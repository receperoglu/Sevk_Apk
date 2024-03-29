package com.eroglu.sevk.network;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


public interface FileUploadService {
    @Multipart
    @POST("mobapi.ashx")
    Single<ResponseBody> onFileUpload(@Part("username") RequestBody mUserName, @Part MultipartBody.Part file);
}
