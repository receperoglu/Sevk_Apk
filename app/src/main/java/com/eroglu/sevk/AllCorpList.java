package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.mindorks.placeholderview.PlaceHolderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//adb connect 192.168.137.54:5555

public class AllCorpList extends AppCompatActivity {
    public String ArticelNameHead;
    public EditText ArticelView;
    public TextView ArtNameTXt;
    public ImageView uploadactivitystart;
    JSONArray ImageListe = null;
    ArrayList<HashMap<String, String>> OrderArray;
    ArrayList<HashMap<String, String>> PictureArray;
    ProgressDialog mProgressDialog;
    private PlaceHolderView mGalleryView;
    private String ArticelName;
    private String ArticelId;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView OrderList;
    private ListView pdflist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_info);
        OrderArray = new ArrayList<>();
        OrderList = findViewById(R.id.CorpList); // listView

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Vergi Bilgileri</span>"));


        new getTalepDetaylari().execute();
    }

    private class getTalepDetaylari extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            HTTPConnection sh = new HTTPConnection();
            String OrderReq = String.valueOf(sh.makeServiceCall("post/CorpList.ashx"));

            if (OrderReq != null) {
                try {


                    JSONObject jsonObject = new JSONObject("{\"Result\":" +  OrderReq+"}");
                    JSONArray Orders = jsonObject.getJSONArray("Result");





                    for (int i = 0; i < Orders.length(); i++) {
                        JSONObject t = Orders.getJSONObject(i);
                        String Name = t.getString("Name");
                        String VergiNo = t.getString("VergiDairesi") + " - " + t.getString("VergiNo");
                        String Adres = t.getString("Adress");

                        HashMap<String, String> OrderMap = new HashMap<>();
                        OrderMap.put("Name", Name);
                        OrderMap.put("VergiNo", VergiNo);
                        OrderMap.put("Adres", Adres);
                        OrderArray.add(OrderMap);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Hata: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "JHata: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "doInBackground: Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server ", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(AllCorpList.this, R.style.Theme_Design_BottomSheetDialog);
            mProgressDialog.setTitle("Siparişler Getiriliyor");
            mProgressDialog.setMessage("Lütfen Bekleyin");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(AllCorpList.this, OrderArray,
                    R.layout.cell_corp_infolist, new String[]{"Name", "VergiNo", "Adres"},
                    new int[]{R.id.Name, R.id.VAT, R.id.Adress});
            OrderList.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }
}


