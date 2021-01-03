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


public class MainActivity extends AppCompatActivity {
    ProgressDialog progressDialog;

    ListAdapter adapter;
    ArrayList<HashMap<String, String>> taleplerimList;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle("Siparişler");


        new getTaleplerim().execute();

        taleplerimList = new ArrayList<>();
        lv = findViewById(R.id.lv);


        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {



                Intent intent = new Intent(view.getContext(), ArticelDetailActivity.class);
                intent.putExtra("ArticelId", ((TextView) view.findViewById(R.id.ArticelId)).getText().toString());
                intent.putExtra("ArticelName", ((TextView) view.findViewById(R.id.ArticelName)).getText().toString());
                intent.putExtra("SaleTypeId", ((TextView) view.findViewById(R.id.SaleType)).getText().toString());
                intent.putExtra("CorpId", ((TextView) view.findViewById(R.id.CorpId)).getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.sl, R.anim.sr);
            }

        });


        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long arg3) {
                progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Lütfen Bekleyin");


                progressDialog.show();


                Intent intent = new Intent(view.getContext(), Create_Order.class);
                intent.putExtra("ArticelId", ((TextView) view.findViewById(R.id.ArticelId)).getText().toString());
                intent.putExtra("ArticelName", ((TextView) view.findViewById(R.id.ArticelName)).getText().toString());
                intent.putExtra("SaleTypeId", ((TextView) view.findViewById(R.id.SaleType)).getText().toString());
                intent.putExtra("CorpId", ((TextView) view.findViewById(R.id.CorpId)).getText().toString());

                startActivity(intent);


                return true;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        ((MenuInflater) inflater).inflate(R.menu.newact, menu);
        return true;
    }


    public void openCustomers(MenuItem item) {


        Intent t = new Intent(this, CorpList.class);
        startActivity(t);
        overridePendingTransition(R.anim.sl, R.anim.sr);
    }

    public void newOrder(MenuItem item) {


        Intent t = new Intent(this, Create_Articel.class);
        startActivity(t);
    }

    public void newCustomer(MenuItem item) {


        Intent t = new Intent(this, CorpArticelActivity.class);
        startActivity(t);
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

            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh = new HTTPConnection();
            String jsonStr = String.valueOf(sh.makeServiceCall("Articels"));
            if (jsonStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    JSONArray taleplerim = jsonObject.getJSONArray("Result");
                    for (int i = 0; i < taleplerim.length(); i++) {
                        JSONObject t = taleplerim.getJSONObject(i);
                        String ArticelId = t.getString("id");
                        String ArticelName = t.getString("ArticelName");
                        String CustomerName = t.getString("CustomerName");
                        String CorpId = t.getString("CorpId");
                        String SaleType = t.getString("SaleType");

                        HashMap<String, String> talepler = new HashMap<>();
                        talepler.put("id", ArticelId);
                        talepler.put("CorpId", CorpId);
                        talepler.put("SaleType", SaleType);

                        talepler.put("Que", ArticelId);
                        talepler.put("ArticelName", ArticelName);
                        talepler.put("CustomerName", CustomerName);
                        taleplerimList.add(talepler);
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
            adapter = new SimpleAdapter(MainActivity.this, taleplerimList,
                    R.layout.item_list, new String[]{"id", "SaleType", "CorpId", "Que", "ArticelName", "CustomerName"},
                    new int[]{R.id.ArticelId, R.id.SaleType, R.id.CorpId, R.id.Que, R.id.ArticelName, R.id.CustomerName});
            lv.setAdapter(adapter);
          progressDialog.dismiss();
        }
    }

}















