package com.eroglu.sevk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
 import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
 import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

public class BigPictureActivity extends Activity {
    public ImageView uploadactivitystart;
    public String Path = "";
    Bitmap pic = null;
    private static final String TAG = HTTPConnection.class.getSimpleName();

    android.graphics.Matrix matrix = new android.graphics.Matrix();
    Float scale = 1f;
    ScaleGestureDetector SGD;
    ImageView image;
     ProgressDialog progressDialog;

    private ScaleGestureDetector mScaleGestureDetector;
    private float mScaleFactor = 1.0f;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bigpicture);
        Bundle bundle = getIntent().getExtras();
        Path = bundle.getString("Path");
        uploadactivitystart = findViewById(R.id.download);
        image = findViewById(R.id.bigpicture);

        mImageView = (ImageView) findViewById(R.id.bigpicture);
        mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
        new DownloadImage().execute(Path);


        uploadactivitystart.setClickable(true);
        uploadactivitystart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SaveAsImage().execute();

            }
        });
    }


    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            mScaleFactor *= scaleGestureDetector.getScaleFactor();
            mScaleFactor = Math.max(0.1f,
                    Math.min(mScaleFactor, 10.0f));
            mImageView.setScaleX(mScaleFactor);
            mImageView.setScaleY(mScaleFactor);
            return true;
        }
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BigPictureActivity.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String imageURL = URL[0];
            try {
                InputStream input = new java.net.URL(imageURL).openStream();
                pic = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                uploadactivitystart.setVisibility(View.VISIBLE);
            }
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            image.setImageBitmap(result);


            progressDialog.dismiss();
        }


    }


    private class SaveAsImage extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BigPictureActivity.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected Bitmap doInBackground(String... URL) {
            String root = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES).toString();
            File myDir = new File(root);
            myDir.mkdirs();
            Random generator = new Random();

            int n = 10000;
            n = generator.nextInt(n);
            String fname = "Image-" + n + ".jpg";
            File file = new File(myDir, fname);

            try {
                FileOutputStream out = new FileOutputStream(file);
                pic.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Log.e(TAG, file.toString());

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "çıkş hatası");
                Log.e(TAG, file.toString());

            }
            return pic;
        }

        @Override
        protected void onPostExecute(Bitmap result) {


            progressDialog.dismiss();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Galeriye Kaydedildi",
                    Toast.LENGTH_SHORT);

            toast.show();
        }


    }


}

