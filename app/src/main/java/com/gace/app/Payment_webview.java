package com.gace.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Payment_webview extends AppCompatActivity {

    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_webview);

        browser = findViewById(R.id.webview);
        browser.setWebViewClient(new MyBrowser());

        browser.loadUrl("https://api.ravepay.co/flwv3-pug/getpaidx/api/flwpbf-inline.js");

//        removed from above due to error
//        PBFPubKey= ;
//        customer_email="user@example.com";
//        amount: 2000;
//        customer_phone: "234099940409";
//        currency: "NGN";
//        txref: "rave-123456";
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
