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
 import android.widget.ImageView;
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


//adb connect 192.168.1.42:5555

public class ArticelDetailActivity extends AppCompatActivity {
     public ImageView uploadactivitystart;
     public  TextView articelfiles;
     ArrayList<HashMap<String, String>> OrderArray;
    ArrayList<HashMap<String, String>> PictureArray;
    ProgressDialog mProgressDialog;
     private String ArticelName;
    private String ArticelId;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView OrderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articel);
        Bundle bundle = getIntent().getExtras();
        ArticelId = bundle.getString("ArticelId");
        ArticelName = bundle.getString("ArticelName");

        uploadactivitystart = findViewById(R.id.uploadactivitystart);
        articelfiles= findViewById(R.id.articelfiles);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOneDrive)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>"+ArticelName+"</span>"));
         PictureArray = new ArrayList<>();

        OrderArray = new ArrayList<>();
        OrderList = findViewById(R.id.OrderList); // listView
        uploadactivitystart.setClickable(true);
        articelfiles.setClickable(true);
        uploadactivitystart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UploadActivity.class);
                intent.putExtra("ArticelId", ArticelId);
                startActivity(intent);
            }
        });

        articelfiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ArticelFiles.class);
                intent.putExtra("ArticelId", ArticelId);
                startActivity(intent);
            }
        });
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
            mProgressDialog = new ProgressDialog(ArticelDetailActivity.this, R.style.Theme_Design_BottomSheetDialog);
            mProgressDialog.setTitle("Siparişler Getiriliyor");
            mProgressDialog.setMessage("Lütfen Bekleyin");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(ArticelDetailActivity.this, OrderArray,
                    R.layout.cellorder_list, new String[]{"OrderId", "Quee", "Product"},
                    new int[]{R.id.OrderId, R.id.Quee, R.id.Product});
            OrderList.setAdapter(adapter);
            mProgressDialog.dismiss();
        }
    }
}


