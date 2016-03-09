package com.uboss.godcodecamera.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.Bimp;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;
import com.uboss.godcodecamera.app.volley.VolleyErrorUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GodCodeWebActivity extends AppCompatActivity {
    private static final String TAG = "GodCodeWebActivity";
    //    @InjectView(R.id.img_test)
//    ImageView img_test;
    @InjectView(R.id.web_test)
    WebView web_test;
    @InjectView(R.id.btn_create_qrcode)
    Button btn_create_qrcode;
    private JSONObject json = new JSONObject();
    private String code,platform,black_code,article_content,poi_uid,poi_city,poi_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        platform = intent.getStringExtra("platform");
        black_code = intent.getStringExtra("black_code");
        article_content = intent.getStringExtra("article_content");
        poi_uid = intent.getStringExtra("poi_uid");
        poi_city = intent.getStringExtra("poi_city");
        poi_name = intent.getStringExtra("poi_name");


//        previewVolleyRequest();
        btn_create_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                    Intent intent_process =  new Intent(GodCodeWebActivity.this, PhotoProcessActivity.class);
//                    //文字描述
//                    if (is_up_edittext)
//                        intent_process.putExtra("content",et_qrcode_content_up.getText().toString());
//                    else
//                        intent_process.putExtra("content",et_qrcode_content_down.getText().toString());
//                    //包含图片张数
//                    intent_process.putExtra("count", Bimp.tempSelectBitmap.size());
//                    //二维码内容地址
//                    intent_process.putExtra("url",url);
//                    startActivity(intent_process);
            }
        });

        String url = getIntent().getStringExtra("url");
        web_test.loadUrl(url,getHeader());
        Uri.decode(CameraActivity.Main_Photo_Name);
//        web_test.loadUrl("http://10.17.1.42:8020/camera_html/mood_html/mood.html");
//        web_test.loadUrl("http://www.taobao.com");
        WebSettings settings = web_test.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setUserAgentString("ubossman");
//        Log.i("cyan","agent::"+settings.getUserAgentString());
//        settings.getUserAgentString();

    }

//    private void previewVolleyRequest() {
//
//
//        StringRequest request = new StringRequest( AppConstants.HOME_URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                VolleyErrorUtil.onErrorResponse(volleyError);
//
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                return getHeader();
//            }
//        };
//        App.startVolley(request);
//    }

    public Map<String, String>  getHeader(){

        Map<String, String> params = new HashMap<String, String>();

        params.put("code",code);
        params.put("platform",platform);
        params.put("black_code",black_code);
        params.put("article_content",article_content);
        params.put("poi_uid",poi_uid);
        params.put("poi_city",poi_city);
        params.put("poi_name",poi_name);
        Log.e(TAG,"params::"+params);
//        params.put("agent","ubossman");
        return params;
    }

}
