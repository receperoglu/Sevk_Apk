package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;

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

public class Create_Order extends AppCompatActivity {

    ProgressDialog progressDialog;
    ListAdapter ProductsListAdapter;

    String jsonStr = "";
    String jsonStrs = "";
    String articelid = "";
    String corpid = "";
    String articelname = "";
    String saletype = "";
    String OrderId = "";
    String selectedProducttype = "";


    ArrayList<HashMap<String, String>> ProductArrayLists;


    private String TAG = Create_Order.class.getSimpleName();


    private Button createbuton;
    private ListView Producttype;
    private EditText oPiece;
    private TextView metric;

    private EditText oDimensions;
    private EditText oColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neworder);
        Bundle bundle = getIntent().getExtras();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Ürün Tipi Seç</span>"));
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        new VoidJson().execute();
        corpid = bundle.getString("CorpId");
        articelid = bundle.getString("ArticelId");
        articelname = bundle.getString("ArticelName");

        saletype = bundle.getString("SaleTypeId");


        ProductArrayLists = new ArrayList<>();
        createbuton = findViewById(R.id.createorder);
        Producttype = findViewById(R.id.producttypelisview);
        oPiece = findViewById(R.id.orderPiece);
        oColor = findViewById(R.id.orderColor);
        oDimensions = findViewById(R.id.orderDimensions);
        metric = findViewById(R.id.metric);

        Producttype.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {

                selectedProducttype = ((TextView) view.findViewById(R.id.ProductTypeId)).getText().toString();
                metric.setText(((TextView) view.findViewById(R.id.MetricTypeName)).getText().toString());
                Producttype.setVisibility(View.GONE);
                createbuton.setVisibility(View.VISIBLE);
                ActionBar actionBar = getSupportActionBar();
                actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar2)));

                actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>Detay Girin</span>"));
            }
        });

        createbuton.setClickable(true);
        createbuton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new SaveOrder().execute();


                progressDialog = new ProgressDialog(Create_Order.this, R.style.Theme_Design_BottomSheetDialog);
                progressDialog.setTitle("Kaydediliyor");
                progressDialog.setMessage("Lütfen Bekleyin");
                progressDialog.setIndeterminate(false);
                progressDialog.show();

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {

                            }
                        }, 1000);


            }
        });


    }


    private class VoidJson extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(Create_Order.this, R.style.Theme_Design_BottomSheetDialog);
            progressDialog.setTitle("Firmalar Listeleniyor");
            progressDialog.setMessage("Lütfen Bekleyin");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (jsonStr != null) {
                HTTPConnection shs = new HTTPConnection();

                jsonStrs = String.valueOf(shs.makeServiceCall("post/ProductType.ashx"));
                if (jsonStrs != null) {
                    try {
                        JSONObject jsonObjects = new JSONObject("{\"Result\":" + jsonStrs + "}");
                        JSONArray CorpListArrays = jsonObjects.getJSONArray("Result");
                        for (int i = 0; i < CorpListArrays.length(); i++) {
                            JSONObject ts = CorpListArrays.getJSONObject(i);

                            HashMap<String, String> CorpLists = new HashMap<>();

                            CorpLists.put("id", ts.getString("id"));
                            CorpLists.put("Name", ts.getString("Name"));
                            CorpLists.put("MetricTypeName", ts.getString("Metrics"));

                            ProductArrayLists.add(CorpLists);


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


            ProductsListAdapter = new SimpleAdapter(Create_Order.this, ProductArrayLists, R.layout.item_producttype,
                    new String[]{"id", "Name", "MetricTypeName"},
                    new int[]{R.id.ProductTypeId, R.id.ProductTypeName, R.id.MetricTypeName});
            Producttype.setAdapter(ProductsListAdapter);

            progressDialog.dismiss();
            progressDialog.hide();
        }
    }


    private class SaveOrder extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {


            HTTPConnection sh = new HTTPConnection();
            OrderId = String.valueOf(sh.makeServiceCall(
                    "post/AddOrder.ashx?CorpId=" +
                            corpid +
                            "&SaleType=" + saletype +
                            "&ArticelId=" + articelid +
                            "&ProductType=" + selectedProducttype +
                            "&Piece=" + oPiece.getText().toString().trim() +
                            "&Color=" + oColor.getText().toString().trim() +
                            "&Dimensions=" + oDimensions.getText().toString().trim() + "&Articel=" + articelname));


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            progressDialog.hide();

            oPiece.setText("");
            oDimensions.setText("");
            oColor.setText("");
            Producttype.setVisibility(View.VISIBLE);
            createbuton.setVisibility(View.INVISIBLE);
        }
    }
}
