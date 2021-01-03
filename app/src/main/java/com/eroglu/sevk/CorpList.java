package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CorpList extends AppCompatActivity {

    ProgressDialog progressDialog;
    ListAdapter adapter;
    String jsonStr = "";

    ArrayList<HashMap<String, String>> CorpArrayList;
    ImageButton isimarama;
    private String TAG = CorpList.class.getSimpleName();
    private ListView lv;
    private String Vergi;
    private String Adress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_corplist);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Firmalar</span>"));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        new getTaleplerim().execute();
        CorpArrayList = new ArrayList<>();
        lv = findViewById(R.id.lv);
         lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                String CorpId = ((TextView) view.findViewById(R.id.CorpId)).getText().toString();
                String CorpName = ((TextView) view.findViewById(R.id.CorpName)).getText().toString();
                Intent intent = new Intent(view.getContext(), CorpArticelActivity.class);
                intent.putExtra("CorpId", CorpId);
                intent.putExtra("CorpName", CorpName);
                intent.putExtra("Vergi", ((TextView) view.findViewById(R.id.VergiDairesi)).getText().toString()+" - "+((TextView) view.findViewById(R.id.VergiNo)).getText().toString());
                intent.putExtra("Adres", ((TextView) view.findViewById(R.id.Adress)).getText().toString());

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

    public void Gallery(MenuItem item) {
        Log.d(TAG, "Galeri Tıklandı");
    }


    public void CorpInfoList(MenuItem item) {
        Intent intent = new Intent(this, AllCorpList.class);
        startActivity(intent);
    }

    public void Photo(MenuItem item) {
        Log.d(TAG, "Paylaş Tıklandı");
    }

    public void Share(MenuItem item) {
        Log.d(TAG, "Paylaş Tıklandı");
    }

    private class getTaleplerim extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CorpList.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh = new HTTPConnection();
            jsonStr = String.valueOf(sh.makeServiceCall("post/CorpList.ashx"));
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject("{\"Result\":" +  jsonStr+"}");
                    JSONArray CorpListArray = jsonObject.getJSONArray("Result");
                    for (int i = 0; i < CorpListArray.length(); i++) {
                        JSONObject t = CorpListArray.getJSONObject(i);
                        String CorpId = t.getString("id");
                        String CorpName = t.getString("Name");
                        HashMap<String, String> CorpList = new HashMap<>();
                        CorpList.put("CorpId", CorpId);
                        CorpList.put("CorpName", CorpName);
                        CorpList.put("VergiNo", t.getString("VergiNo"));
                        CorpList.put("Adress", t.getString("Adress"));
                        CorpList.put("VergiDairesi", t.getString("VergiDairesi"));



                        CorpArrayList.add(CorpList);
                    }
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
            adapter = new SimpleAdapter(CorpList.this, CorpArrayList, R.layout.corp_list,
                    new String[]{"CorpId", "CorpName", "VergiDairesi","VergiNo","Adress"},
                    new int[]{R.id.CorpId, R.id.CorpName,R.id.VergiDairesi,R.id.VergiNo,R.id.Adress});
            lv.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}
