package com.rifaqat.knowledgesharing.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import com.rifaqat.knowledgesharing.R;

import java.net.URLEncoder;
import java.util.Objects;

public class viewpdf extends AppCompatActivity {

    WebView pdfview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpdf);

        //Hide toolbar.
        Objects.requireNonNull(getSupportActionBar()).hide();

        pdfview = (WebView)findViewById(R.id.viewpdf);
        pdfview.getSettings().setJavaScriptEnabled(true);

        String filename = getIntent().getStringExtra("filename");
        String fileurl = getIntent().getStringExtra("fileurl");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle(filename);
        pd.setMessage("Opening....!!!");

        pdfview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pd.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pd.dismiss();
            }
        });

        String url="";
        try {
            url= URLEncoder.encode(fileurl,"UTF-8");
        } catch (Exception ex) {}

        pdfview.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
    }

    @Override
    public void onBackPressed() {
        if (pdfview.canGoBack()) {
            pdfview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
