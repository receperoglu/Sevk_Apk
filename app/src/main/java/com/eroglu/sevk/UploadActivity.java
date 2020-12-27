package com.eroglu.sevk;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eroglu.sevk.network.FileUploaderContract;
import com.eroglu.sevk.network.FileUploaderModel;
import com.eroglu.sevk.network.FileUploaderPresenter;
import com.eroglu.sevk.network.ServiceGenerator;
import com.eroglu.sevk.picker.ImageContract;
import com.eroglu.sevk.picker.ImagePresenter;
import com.eroglu.sevk.utils.CommonUtils;
import com.eroglu.sevk.utils.FileCompressor;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class UploadActivity extends AppCompatActivity implements ImageContract.View, FileUploaderContract.View {
    static final int REQUEST_TAKE_PHOTO = 1001;
    static final int REQUEST_GALLERY_PHOTO = 1002;
    private static final String TAG = "";
    static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    ImageButton galleryselect;
    ImageButton userProfilePhoto;
    String ArticelId;
    ImageView preview;
    Button uploadFileProgress;
    File mPhotoFile;
    private ImagePresenter mImagePresenter;
    private FileUploaderPresenter mUploaderPresenter;
    private FileCompressor mCompressor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_activity);
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        ArticelId = bundle.getString("ArticelId");


        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOneDrive)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Siparişe Fotoğraf/Resim Ekleyin</span>"));



        userProfilePhoto = findViewById(R.id.newphoto);
        galleryselect = findViewById(R.id.galleryselect);
        preview = findViewById(R.id.preview);
        uploadFileProgress = findViewById(R.id.upload_file_progress);
        mImagePresenter = new ImagePresenter(this);
        mUploaderPresenter = new FileUploaderPresenter(this, new FileUploaderModel(ServiceGenerator.createService()));
        mCompressor = new FileCompressor(this);
        uploadFileProgress.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        mUploaderPresenter.onFileSelectedWithoutShowProgress(mImagePresenter.getImage(), ArticelId, "info@androidwave");
                    }
                }
        );
        userProfilePhoto.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        mImagePresenter.cameraClick();
                    }
                }
        );

        galleryselect.setOnClickListener(
                new ImageButton.OnClickListener() {
                    public void onClick(View v) {
                        mImagePresenter.chooseGalleryClick();
                    }
                }
        );


    }


    private void selectImage() {
        final CharSequence[] items = {getString(R.string.take_photo), getString(R.string.choose_gallery),
                getString(R.string.cancel)};
        AlertDialog.Builder builder = new AlertDialog.Builder(UploadActivity.this);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Capture Photo")) {
                mImagePresenter.cameraClick();
            } else if (items[item].equals("Choose from Library")) {
                mImagePresenter.chooseGalleryClick();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public boolean checkPermission() {
        for (String mPermission : permissions) {
            int result = ActivityCompat.checkSelfPermission(this, mPermission);
            if (result == PackageManager.PERMISSION_DENIED) return false;
        }
        return true;
    }

    @Override
    public void showPermissionDialog(boolean isGallery) {
        Dexter.withActivity(this).withPermissions(permissions)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isGallery) {
                                mImagePresenter.chooseGalleryClick();
                            } else {
                                mImagePresenter.cameraClick();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).withErrorListener(error -> showErrorDialog())
                .onSameThread()
                .check();
    }

    @Override
    public File getFilePath() {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    }

    @Override
    public void openSettings() {

    }

    @Override
    public void startCamera(File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (file != null) {
                Uri mPhotoURI = FileProvider.getUriForFile(this,
                        "com.eroglu.sevk"+ ".provider", file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                mPhotoFile = file;
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    @Override
    public void chooseGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, REQUEST_GALLERY_PHOTO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                try {
                    File resultedFile = mCompressor.compressToFile(mPhotoFile);
                    mImagePresenter.saveImage(resultedFile.getPath());
                    mImagePresenter.showPreview(resultedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (requestCode == REQUEST_GALLERY_PHOTO) {
                Uri selectedImage = data.getData();
                try {
                    File resultedFile = mCompressor.compressToFile(new File(Objects.requireNonNull(getRealPathFromUri(selectedImage))));
                    mImagePresenter.saveImage(resultedFile.getPath());
                    mImagePresenter.showPreview(resultedFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    @Override
    public File newFile() {
        Calendar cal = Calendar.getInstance();
        long timeInMillis = cal.getTimeInMillis();
        String mFileName = timeInMillis + ".jpg";
        File mFilePath = getFilePath();
        try {
            File newFile = new File(mFilePath.getAbsolutePath(), mFileName);
            newFile.createNewFile();
            return newFile;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void showErrorDialog() {
        Toast.makeText(getApplicationContext(), getString(R.string.error_message), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayImagePreview(File mFile) {
        Glide.with(UploadActivity.this).load(mFile).apply(new RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.pencil)).into(preview);
    }


    @Override
    public String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(columnIndex);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.message_need_permission));
        builder.setMessage(getString(R.string.message_grant_permission));
        builder.setPositiveButton(getString(R.string.label_setting), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }


    @Override
    public void showErrorMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void uploadCompleted() {
        CommonUtils.hideLoading();
        Toast.makeText(getApplicationContext(), getString(R.string.file_upload_successful), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setUploadProgress(int progress) {

        //txtProgress.setText("Uploading ..." + String.valueOf(progress));
    }


}
