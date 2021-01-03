package com.eroglu.sevk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


 
public class ArticelDetailActivity extends AppCompatActivity {

     ArrayList<HashMap<String, String>> OrderArray;
    ArrayList<HashMap<String, String>> PictureArray;
    ProgressDialog progressDialog;
     private String ArticelName;
    private String ArticelId;
    private  String CorpId;
    private String SaleTypeId;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView OrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articel);
        Bundle bundle = getIntent().getExtras();
        ArticelId = bundle.getString("ArticelId");
        ArticelName = bundle.getString("ArticelName");

        CorpId = bundle.getString("CorpId");

        SaleTypeId = bundle.getString("SaleTypeId");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bluevar1)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>"+ArticelName+"</span>"));
         PictureArray = new ArrayList<>();

        OrderArray = new ArrayList<>();
        OrderList = findViewById(R.id.OrderList); // listView



        OrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String OrderId = ((TextView) view.findViewById(R.id.OrderId)).getText().toString();
                String OrderName = ((TextView) view.findViewById(R.id.Product)).getText().toString();

                Intent intent = new Intent(view.getContext(), ArticelProductMotionActivity.class);
                intent.putExtra("OrderId", OrderId);
                intent.putExtra("OrderName", OrderName);

                startActivity(intent);
            }
        });

        new getTalepDetaylari().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        ((MenuInflater) inflater).inflate(R.menu.articeldetailmenu, menu);
        return true;
    }










    public void openFiles(MenuItem item) {

        Intent intent = new Intent(this, ArticelFiles.class);
        intent.putExtra("ArticelId", ArticelId);
        startActivity(intent);
        overridePendingTransition(R.anim.sl, R.anim.sr);
    }

    public void addPhotos(MenuItem item) {
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra("ArticelId", ArticelId);
        startActivity(intent);
        overridePendingTransition(R.anim.sl, R.anim.sr);
    }

    public void newOrder(MenuItem item) {
        Intent t = new Intent(this, Create_Articel.class);
        startActivity(t);
    }
    public void producutOut(MenuItem item) {



        Intent t = new Intent(this, CorpArticelActivity.class);
        startActivity(t);
    }
    public void newProduct(MenuItem item) {
        Intent intent = new Intent(this, Create_Order.class);
        intent.putExtra("ArticelId", ArticelId);
        intent.putExtra("ArticelName", ArticelName);
        intent.putExtra("SaleTypeId",SaleTypeId);
        intent.putExtra("CorpId", CorpId);
        startActivity(intent);
    }




    private class getTalepDetaylari extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            HTTPConnection sh = new HTTPConnection();
            String OrderReq = String.valueOf(sh.makeServiceCall("Orders&ArticelId=" + ArticelId));

            if (OrderReq != null) {
                try {
                    JSONObject jsonObject = new JSONObject(OrderReq);
                    JSONArray Orders = jsonObject.getJSONArray("Result");
                    if (Orders == null) {
                        Log.d(TAG, " Order Empty");
                    }
                    for (int i = 0; i < Orders.length(); i++) {
                        JSONObject t = Orders.getJSONObject(i);
                        String OrderId = t.getString("id");
                        String Piece = t.getString("Piece");
                        String Dimensions = t.getString("Dimensions");
                        String ProductTypeName = t.getString("ProductTypeName");
                        String Color = t.getString("Color");
                        String Metrics = t.getString("Metrics");
                        String CorpId = t.getString("CorpId");
                        String SaleTypeId = t.getString("SaleTypeId");
                        HashMap<String, String> OrderMap = new HashMap<>();
                        OrderMap.put("OrderId", OrderId);
                        OrderMap.put("Product", Piece + " " + Metrics + " " + Dimensions + " " + Color + " " + ProductTypeName);
                        OrderMap.put("CorpId", CorpId);
                        OrderMap.put("SaleTypeId", SaleTypeId);
                        OrderMap.put("Quee", String.valueOf(i + 1));
                        OrderArray.add(OrderMap);


                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Order Error " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Order Error " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, " Json  Sunucu Hatası.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json Sunucu Hatası ", Toast.LENGTH_LONG).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ArticelDetailActivity.this);
            progressDialog.setMessage("Lütfen Bekleyin");


            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(ArticelDetailActivity.this, OrderArray,
                    R.layout.cellorder_list, new String[]{"OrderId", "Quee", "Product"},
                    new int[]{R.id.OrderId, R.id.Quee, R.id.Product});
            OrderList.setAdapter(adapter);
            progressDialog.dismiss();
        }
    }
}


