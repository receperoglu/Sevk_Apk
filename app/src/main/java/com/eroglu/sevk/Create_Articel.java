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
import android.widget.Button;
import android.widget.EditText;
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

import okio.Timeout;

public class Create_Articel extends AppCompatActivity {

    ProgressDialog mProgressDialog;
    ListAdapter adapter;
    ListAdapter SalesListAdapter;

    String jsonStr = "";
    String jsonStrs = "";
    String articelid = "";
    String artname="";

    String selectedcorp="";
    String selectedsaletype="";
    ArrayList<HashMap<String, String>> CorpArrayList;
    ArrayList<HashMap<String, String>> SaleArrayLists;



     private String TAG = Create_Articel.class.getSimpleName();
    private ListView lv;
    private EditText ordername;

    private EditText newarticelid;

    private Button createbuton;
    private ListView saletype;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newarticel);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOneDrive)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Firma Seç</span>"));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        new getTaleplerim().execute();
        CorpArrayList = new ArrayList<>();

        SaleArrayLists = new ArrayList<>();
        lv = findViewById(R.id.lv);
        createbuton=findViewById(R.id.create);
        saletype=findViewById(R.id.saletypelisview);
         ordername=findViewById(R.id.ordername);
        newarticelid=findViewById(R.id.newarticelid);
         lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

               selectedcorp = ((TextView) view.findViewById(R.id.CorpId)).getText().toString();


                lv.setVisibility(View.GONE);
                saletype.setVisibility(View.VISIBLE);
             }
        });

        saletype.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                selectedsaletype = ((TextView) view.findViewById(R.id.SaleTypeId)).getText().toString();

                createbuton.setVisibility(View.VISIBLE);
                saletype.setVisibility(View.GONE);
                 createbuton.setVisibility(View.VISIBLE);
                 ordername.setVisibility(View.VISIBLE);
              }
        });

        createbuton.setClickable(true);

        createbuton.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {

                new CreateArticel().execute();

                 mProgressDialog = new ProgressDialog(Create_Articel.this, R.style.Theme_Design_BottomSheetDialog);
                 mProgressDialog.setTitle("Artikel Oluşturuluyor");
                 mProgressDialog.setMessage("Lütfen Bekleyin");
                 mProgressDialog.setIndeterminate(false);
                 mProgressDialog.show();

                 new android.os.Handler().postDelayed(
                         new Runnable() {
                             public void run() {
                                 Intent intent = new Intent(v.getContext(), Create_Order.class);
                                 intent.putExtra("ArticelId",  articelid);
                                 intent.putExtra("ArticelName",artname );
                                 intent.putExtra("CorpId", selectedcorp);
                                 intent.putExtra("SaleTypeId", selectedsaletype);

                                 startActivity(intent);
                             }
                         }, 5000);




            }
        });




    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }



    private class CreateArticel extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(Create_Articel.this, R.style.Theme_Design_BottomSheetDialog);
            mProgressDialog.setTitle("Artikel Oluşturuluyor");
            mProgressDialog.setMessage("Lütfen Bekleyin");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HTTPConnection sh = new HTTPConnection();
            articelid = String.valueOf(sh.makeServiceCall("post/AddArticel.ashx?CorpId="+
                    selectedcorp+"&SaleType="+selectedsaletype+"&Articel="+ordername.getText() ));
              overridePendingTransition(R.anim.sl, R.anim.sr);


            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            mProgressDialog.dismiss();
        }
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
            mProgressDialog = new ProgressDialog(Create_Articel.this, R.style.Theme_Design_BottomSheetDialog);
            mProgressDialog.setTitle("Firmalar Listeleniyor");
            mProgressDialog.setMessage("Lütfen Bekleyin");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
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


                    HTTPConnection shs = new HTTPConnection();

                     jsonStrs = String.valueOf(shs.makeServiceCall("post/SaleType.ashx"));
                    if (jsonStrs != null) {
                        try {
                            JSONObject jsonObjects = new JSONObject("{\"Result\":" +  jsonStrs+"}");
                            JSONArray CorpListArrays = jsonObjects.getJSONArray("Result");
                            for (int i = 0; i < CorpListArrays.length(); i++) {
                                JSONObject ts = CorpListArrays.getJSONObject(i);

                                HashMap<String, String> CorpLists = new HashMap<>();

                                CorpLists.put("id", ts.getString("id"));
                                CorpLists.put("Name", ts.getString("Name"));



                                SaleArrayLists.add(CorpLists);



                            }}catch (final JSONException e) {
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
            adapter = new SimpleAdapter(Create_Articel.this, CorpArrayList, R.layout.corp_list,
                    new String[]{"CorpId", "CorpName", "VergiDairesi","VergiNo","Adress"},
                    new int[]{R.id.CorpId, R.id.CorpName,R.id.VergiDairesi,R.id.VergiNo,R.id.Adress});
            lv.setAdapter(adapter);

            SalesListAdapter = new SimpleAdapter(Create_Articel.this, SaleArrayLists, R.layout.item_saletype,
                    new String[]{"id", "Name"},
                    new int[]{R.id.SaleTypeId, R.id.SaleTypeName});
            saletype.setAdapter(SalesListAdapter);


            mProgressDialog.dismiss();
        }
    }
}
