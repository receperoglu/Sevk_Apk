package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CorpArticelActivity extends AppCompatActivity {
    public String CorpName;
    public String CorpId;
    ProgressDialog progressDialog;
    ListAdapter adapter;
    String jsonStr = "";
    private TextView tablobaslik;
    private String Vergi;

    public static ArrayList<HashMap<String, String>> taleplerimList;
    private String TAG = CorpArticelActivity.class.getSimpleName();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corp_articels);
        Bundle bundle = getIntent().getExtras();
        CorpId = bundle.getString("CorpId");
        CorpName = bundle.getString("CorpName");
        Vergi=bundle.getString("Adres")+" " +bundle.getString("Vergi");



        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>"+CorpName+"</span>"));



        tablobaslik = new TextView(this);
        tablobaslik.setTextSize(16);
        tablobaslik.setTextColor(Color.parseColor("#ffffff"));
        tablobaslik.setBackgroundColor(Color.parseColor("#0078d4"));
        tablobaslik.setHeight(150);
        tablobaslik.setPadding(10,20,10,5);






        getSupportActionBar().setLogo(R.drawable.photo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);


        new getTaleplerim().execute();

        taleplerimList = new ArrayList<>();
        lv = findViewById(R.id.lv);






        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String Articel = ((TextView) view.findViewById(R.id.ArticelId)).getText().toString();
                String ArticelName = ((TextView) view.findViewById(R.id.ArticelName)).getText().toString();
                Intent intent = new Intent(view.getContext(), ArticelDetailActivity.class);
                intent.putExtra("ArticelId", Articel);
                intent.putExtra("ArticelName", ArticelName);
                startActivity(intent);
                overridePendingTransition(R.anim.sl, R.anim.sr);
            }

        });




    }




    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void CorpInfoList(MenuItem item) {
        Intent intent = new Intent(this, AllCorpList.class);
        startActivity(intent);
    }

    public void Gallery(MenuItem item) {
        Log.d(TAG, "Galeri Tıklandı");
    }

    public void Photo(MenuItem item) {
        Log.d(TAG, "Foto Tıklandı");
    }

    public void Share(MenuItem item) {
        Log.d(TAG, "Paylaş Tıklandı");
    }

    private class getTaleplerim extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CorpArticelActivity.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh = new HTTPConnection();
            jsonStr = String.valueOf(sh.makeServiceCall("CorpArticels&CorpId=" + CorpId));
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray taleplerim = jsonObject.getJSONArray("Result");
                    for (int i = 0; i < taleplerim.length(); i++) {
                        JSONObject t = taleplerim.getJSONObject(i);
                        String ArticelId = t.getString("id");
                        String ArticelName = t.getString("ArticelName");
                        String CustomerName = t.getString("CustomerName");
                        HashMap<String, String> talepler = new HashMap<>();
                        talepler.put("id", ArticelId);
                        talepler.put("Que", "AT - " + ArticelId);
                        talepler.put("ArticelName", ArticelName.toLowerCase());
                        talepler.put("CustomerName", "");
                        taleplerimList.add(talepler);




                    }

                    tablobaslik.setText(Vergi);
                    tablobaslik.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    lv.addHeaderView(tablobaslik);
                } catch (final JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            }
                            getApplicationContext().startActivity(intent);
                        }
                    });
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getApplicationContext(), ErrorActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        }
                        getApplicationContext().startActivity(intent);
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            adapter = new SimpleAdapter(CorpArticelActivity.this, taleplerimList,
                    R.layout.itemcorpactivity, new String[]{"id", "Que", "ArticelName"},
                    new int[]{R.id.ArticelId, R.id.Que, R.id.ArticelName});
            lv.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }


}
