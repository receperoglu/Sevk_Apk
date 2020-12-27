package com.eroglu.sevk.network;


import androidx.annotation.NonNull;

import java.io.File;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;


public class FileUploaderModel implements FileUploaderContract.Model {
    private final FileUploadService service;
    public static final String MULTIPART_FORM_DATA = "multipart/form-data";

    public FileUploaderModel(FileUploadService service) {
        this.service = service;
    }

    /**
     * Create request body for image resource
     *
     * @param file
     * @return
     */
    private RequestBody createRequestForImage(File file) {
        return RequestBody.create(MediaType.parse("image/*"), file);
    }

    /**
     * Create request body for video resource
     *
     * @param file
     * @return
     */
    private RequestBody createRequestForVideo(File file) {
        return RequestBody.create(MediaType.parse("video/*"), file);
    }

    /**
     * Create request body for string
     *
     * @param descriptionString
     * @return
     */
    @NonNull
    private RequestBody createPartFromString(String descriptionString) {
        return RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), descriptionString);
    }

    /**
     * return multipart part request body
     *
     * @param filePath
     * @return
     */
    private MultipartBody.Part createMultipartBody(String filePath) {
        File file = new File(filePath);
        RequestBody requestBody = createRequestForImage(file);
        return MultipartBody.Part.createFormData("file_name", file.getName(), requestBody);
    }

    /**
     * return multi part body in format of FlowableEmitter
     *
     * @param filePath
     * @param emitter
     * @return
     */
    private MultipartBody.Part createMultipartBody(String filePath, FlowableEmitter<Double> emitter) {
        File file = new File(filePath);
        return MultipartBody.Part.createFormData("file_name", file.getName(), createCountingRequestBody(file, emitter));
    }

    private RequestBody createCountingRequestBody(File file, FlowableEmitter<Double> emitter) {
        RequestBody requestBody = createRequestForImage(file);
        return new CountingRequestBody(requestBody, (bytesWritten, contentLength) -> {
            double progress = (1.0 * bytesWritten) / contentLength;
            emitter.onNext(progress);
        });
    }


    @Override
    public Flowable<Double> uploadFile(String selectedFile, String username, String email) {
        RequestBody mUserName = createPartFromString(username);
        RequestBody mEmail = createPartFromString(email);
        return Flowable.create(emitter -> {
            try {
                ResponseBody response = service.onFileUpload(mUserName, createMultipartBody(selectedFile, emitter)).blockingGet();
                emitter.onComplete();
            } catch (Exception e) {
                emitter.tryOnError(e);
            }
        }, BackpressureStrategy.LATEST);
    }

    @Override
    public Single<ResponseBody> uploadFileWithoutProgress(String filePath, String username, String email) {
        RequestBody mUserName = createPartFromString(username);
        RequestBody mEmail = createPartFromString(email);
        return service.onFileUpload(mUserName, createMultipartBody(filePath));
    }
}