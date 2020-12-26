package com.eroglu.sevk;


import android.os.Bundle;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;


public class webviewActivity extends AppCompatActivity {
    public String siteurl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        Bundle bundle = getIntent().getExtras();
        siteurl = bundle.getString("Path");
        WebView webView = (WebView) findViewById(R.id.tara);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(siteurl);


    }


}


