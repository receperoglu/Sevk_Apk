package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.eroglu.sevk.gallery.ImageTypeBig;
import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.eroglu.sevk.DomainName.getURL;

//adb connect 192.168.1.42:5555

public class ArticelFiles extends AppCompatActivity {

    JSONArray ImageListe = null;
    ArrayList<HashMap<String, String>> PictureArray;
    ProgressDialog progressDialog;
    private PlaceHolderView mGalleryView;
    private String ArticelId;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView pdflist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);
        Bundle bundle = getIntent().getExtras();
        ArticelId = bundle.getString("ArticelId");


        pdflist = findViewById(R.id.pdflist);
        PictureArray = new ArrayList<>();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Sipariş Dosyaları</span>"));

        mGalleryView = findViewById(R.id.galleryView);


        pdflist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String FilePath = ((TextView) view.findViewById(R.id.Product)).getText().toString();


                Intent intent = new Intent(view.getContext(), webviewActivity.class);
                intent.putExtra("Path", "https://docs.google.com/viewer?url=" + getURL() + "/abi/dosyalar/" + FilePath);
                startActivity(intent);


            }
        });
        new getTalepDetaylari().execute();
    }

    private class getTalepDetaylari extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh2 = new HTTPConnection();
            String ImageConn = String.valueOf(sh2.makeServiceCall("Pictures&ArticelId=" + ArticelId));
            try {
                JSONObject jsonObject2 = new JSONObject(ImageConn);
                ImageListe = jsonObject2.getJSONArray("Result");
                for (int i = 0; i < ImageListe.length(); i++) {
                    JSONObject t = ImageListe.getJSONObject(i);
                    String Path = t.getString("Path");
                    HashMap<String, String> pdfMap = new HashMap<>();
                    if (Path.contains(".pdf")) {
                        pdfMap.put("Path", Path);
                        pdfMap.put("Quee", ">");
                        PictureArray.add(pdfMap);
                    }
                }
            } catch (final JSONException e) {
                Log.e(TAG, "Pictures Error" + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Pictures Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            }


            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ArticelFiles.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            List<Image> imageList = ImageDownloader.loadImages(getApplicationContext()
                    , ImageListe);
            List<Image> newImageList = new ArrayList<>();
            for (int i = 0; i < (imageList.size() > 10 ? 10 : imageList.size()); i++) {
                newImageList.add(imageList.get(i));
            }
            for (int i = imageList.size() - 1; i >= 0; i--) {
                String url = imageList.get(i).getImageUrl();
                if (url.contains(".pdf") || url.contains(".xls")) {
                    ListAdapter adapter2 = new SimpleAdapter(ArticelFiles.this, PictureArray,
                            R.layout.cellorder_list, new String[]{"Quee", "Quee", "Path"},
                            new int[]{R.id.OrderId, R.id.Quee, R.id.Product});
                    pdflist.setAdapter(adapter2);
                } else {
                    mGalleryView.addView(new ImageTypeBig(getApplicationContext(), mGalleryView, imageList.get(i).getImageUrl()));
                }
            }

            progressDialog.dismiss();
        }
    }
}

