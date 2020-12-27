package com.eroglu.sevk.picker;

import java.io.File;

public class ImagePresenter implements ImageContract.Presenter {

    private final ImageContract.View view;
    private String selectedFile;

    public ImagePresenter(ImageContract.View view) {
        this.view = view;
    }


    @Override
    public void cameraClick() {
        if (!view.checkPermission()) {
            view.showPermissionDialog(false);
            return;
        }

        File file = view.newFile();

        if (file == null) {
            view.showErrorDialog();
            return;
        }

        view.startCamera(file);
    }

    @Override
    public void chooseGalleryClick() {
        if (!view.checkPermission()) {
            view.showPermissionDialog(true);
            return;
        }

        view.chooseGallery();
    }

    @Override
    public void saveImage(String path) {
        selectedFile = path;
    }

    @Override
    public String getImage() {
        return selectedFile;
    }

    @Override
    public void showPreview(File mFile) {
        view.displayImagePreview(mFile);
    }

}

