package com.eroglu.sevk;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import static com.eroglu.sevk.DomainName.getURL;

public class HTTPConnection {
    private static final String TAG = HTTPConnection.class.getSimpleName();

    public HTTPConnection() {
    }

    public String makeServiceCall(String param) {
        String baseurl = getURL();
        String response = null;
        try {
            String parametre="";

            if(param=="post/CorpList.ashx"){

                parametre = "https://statu.space/abi/post/CorpList.ashx";
            }
           else if(param=="post/SaleType.ashx"){

                parametre = "https://statu.space/abi/post/SaleType.ashx";
            }
            else if(param.contains( "post/AddArticel.ashx")){

                parametre = "https://statu.space/abi/"+param;

            }
            else if(param.contains( "post/AddOrder.ashx")){

                parametre = "https://statu.space/abi/"+param;

            }
            else if(param.contains( "post/ProductType.ashx")){

                parametre = "https://statu.space/abi/"+param;

            }



            else{

                parametre = baseurl+param;
            }

            URL url = new URL(parametre);

            Log.e(TAG, "Site Adresi  : " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}