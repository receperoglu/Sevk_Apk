package com.eroglu.sevk.network;

import android.text.TextUtils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on : Dec 30, 2018
 * Author     : AndroidWave
 * Website    : https://androidwave.com/
 */
public class FileUploaderPresenter implements FileUploaderContract.Presenter {

    private final FileUploaderContract.Model model;
    private final FileUploaderContract.View view;

    private Disposable videoUploadDisposable;

    public FileUploaderPresenter(FileUploaderContract.View view, FileUploaderContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onFileSelected(String selectedFilePath, String userName, String email) {
        if (TextUtils.isEmpty(selectedFilePath)) {
            view.showErrorMessage("Incorrect file path");
            return;
        }
        videoUploadDisposable = model.uploadFile(selectedFilePath, userName, email)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        progress -> view.setUploadProgress((int) (100 * progress)),
                        error -> view.showErrorMessage(error.getMessage()),
                        () -> view.uploadCompleted()
                );
    }

    @Override
    public void onFileSelectedWithoutShowProgress(String selectedFilePath, String userName, String email) {
        if (TextUtils.isEmpty(selectedFilePath)) {
            view.showErrorMessage("Incorrect file path");
            return;
        }
        videoUploadDisposable = model.uploadFileWithoutProgress(selectedFilePath, userName, email)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> view.uploadCompleted(),
                        error -> view.showErrorMessage(error.getMessage())
                );
    }

    @Override
    public void cancel() {
        if (videoUploadDisposable != null && !videoUploadDisposable.isDisposed()) {
            videoUploadDisposable.dispose();
        }
    }
}
