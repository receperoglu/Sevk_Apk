package com.eroglu.sevk;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ArticelProductMotionActivity extends AppCompatActivity {

    public String ArticelNameHead;
    public TextView irsaliyebilgisiyok;
    ArrayList<HashMap<String, String>> OrderArray;
    ArrayList<HashMap<String, String>> tekliflerimList;
    ImageView resims;
    private String ArticelId;
    private String TAG = MainActivity.class.getSimpleName();
    private ListView OrderList;
    private TextView tablobaslik;

    public static String GetFormattedTimeSpan(final long ms) {
        long x = ms / 1000;
        long seconds = x % 60;
        x /= 60;
        long minutes = x % 60;
        x /= 60;
        long hours = x % 24;
        x /= 24;
        long days = x;
        return String.format("%d days %d hours %d minutes %d seconds", days, hours, minutes, seconds);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.motionmain);
        tablobaslik = new TextView(this);
        tablobaslik.setTextSize(20);
        tablobaslik.setTextColor(Color.parseColor("#3c763d"));
        tablobaslik.setBackgroundColor(Color.parseColor("#d6e9c6"));
         tablobaslik.setHeight(200);
         tablobaslik.setPadding(250,50,100,20);
        tablobaslik.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        Bundle bundle = getIntent().getExtras();






        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOneDrive)));

        actionBar.setTitle(Html.fromHtml("<span style='color:#ffffff'>"+bundle.getString("OrderName")+"</span>"));

        ArticelId = bundle.getString("OrderId");
        OrderArray = new ArrayList<>();
        OrderList = findViewById(R.id.ListMotion);
         OrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
                String OrderId = ((TextView) view.findViewById(R.id.OrderId)).getText().toString();
                Log.d(TAG, OrderId);
                Intent intent = new Intent(view.getContext(), ArticelProductMotionActivity.class);
                intent.putExtra("OrderId", OrderId);

                startActivity(intent);
            }
        });

        new getTalepDetaylari().execute();
    }

    private class getTalepDetaylari extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            HTTPConnection sh = new HTTPConnection();
            int totalpiece=0;
            int totalweight=0;
            String OrderReq = String.valueOf(sh.makeServiceCall("Motion&MotionType=One&OrderId=" + ArticelId));
            if (OrderReq != null) {
                try {
                    JSONObject jsonObject = new JSONObject(OrderReq);
                    JSONArray Orders = jsonObject.getJSONArray("Result");
                    for (int i = 0; i < Orders.length(); i++) {
                        JSONObject t = Orders.getJSONObject(i);
                        String Weight = t.getString("Weight");
                        String WayBillId = t.getString("WayBillId");
                        String Color = t.getString("Color");
                        String Piece = t.getString("Piece");



                          HashMap<String, String> OrderMap = new HashMap<>();
                        OrderMap.put("Weight", Weight + " KG");
                        OrderMap.put("CreatedDate", t.getString("CreatedDate") );
                        OrderMap.put("WayBillId", WayBillId);
                        OrderMap.put("Color", Color);
                        OrderMap.put("Piece", Piece + " AD");
                        totalpiece = totalpiece+   Integer.parseInt( Piece);
                        totalweight= totalweight+   Integer.parseInt(Weight);
                        OrderArray.add(OrderMap);
                    }

                    if (Orders.length() == 0) {
                        Toast.makeText(getApplicationContext(),
                                "İrsaliye Kesilmemiş ", Toast.LENGTH_LONG).show();                    }

                    else {
                         tablobaslik.setText(totalpiece + " Adet " +totalweight + " Kg Satıldı." );
                        tablobaslik.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                         OrderList.addHeaderView(tablobaslik);



                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
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
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            ListAdapter adapter = new SimpleAdapter(ArticelProductMotionActivity.this, OrderArray,
                    R.layout.cellmotion, new String[]{"Piece","CreatedDate", "Weight", "WayBillId", "Color"},
                    new int[]{R.id.Piece,R.id.CreatedDate, R.id.Weight, R.id.WayBillId, R.id.Color});
            OrderList.setAdapter(adapter);
        }
    }

}
