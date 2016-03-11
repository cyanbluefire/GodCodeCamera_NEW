package com.uboss.godcodecamera.app.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.MyFileUtils;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;

import org.json.JSONObject;

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
    private int use_model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_godcodeweb);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        platform = intent.getStringExtra("platform");
        black_code = intent.getStringExtra("black_code");
        article_content = intent.getStringExtra("article_content");
        poi_uid = intent.getStringExtra("poi_uid");
        poi_city = intent.getStringExtra("poi_city");
        poi_name = intent.getStringExtra("poi_name");
        use_model = intent.getIntExtra("use_model",1);

//        previewVolleyRequest();
        btn_create_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GodCodeWebActivity.this,PhotoProcessActivity.class);
                intent.putExtra("code",code);
                intent.putExtra("platform",platform);
                intent.putExtra("black_code",black_code);
                intent.putExtra("article_content",article_content);
                intent.putExtra("poi_uid",poi_uid);
                intent.putExtra("poi_city", MyFileUtils.string2Unicode(poi_city));
                intent.putExtra("poi_name",MyFileUtils.string2Unicode(poi_name));
                intent.putExtra("use_model",use_model);
                startActivity(intent);
                finish();

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
        StringBuilder url = new StringBuilder(AppConstants.HOME_URL);
        url.append("templates/").append(use_model).append("/preview");
        Log.i(TAG,"url=="+url.toString());

        WebSettings settings = web_test.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);//设定支持viewport

        web_test.loadUrl(url.toString(),getHeader());
//        Uri.decode(CameraActivity.Main_Photo_Name);
//        web_test.loadUrl("http://10.17.1.42:8020/camera_html/mood_html/mood.html");
//        web_test.loadUrl("http://www.taobao.com");

//        settings.setUserAgentString("ubossman");
//        Log.i("cyan","agent::"+settings.getUserAgentString());
//        settings.getUserAgentString();

        // webview设置
        web_test.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                Log.i(TAG,"shouldOverrideUrlLoading url=="+url);

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i(TAG,"onPageStarted url=="+url);
            }
        });
        //chrome alert
        web_test.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result)
            {

                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }

        });
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
