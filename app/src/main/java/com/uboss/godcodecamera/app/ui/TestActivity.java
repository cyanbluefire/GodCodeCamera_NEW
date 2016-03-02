package com.uboss.godcodecamera.app.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TestActivity extends AppCompatActivity {
//    @InjectView(R.id.img_test)
//    ImageView img_test;
    @InjectView(R.id.web_test)
    WebView web_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);

        Uri.decode(CameraActivity.Main_Photo_Name);
        web_test.loadUrl("http://10.17.1.42:8020/camera_html/mood_html/mood.html");
//        web_test.loadUrl("http://www.taobao.com");
        WebSettings settings = web_test.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUserAgentString("ubossman");
        Log.i("cyan","agent::"+settings.getUserAgentString());
//        settings.getUserAgentString();

    }
}
