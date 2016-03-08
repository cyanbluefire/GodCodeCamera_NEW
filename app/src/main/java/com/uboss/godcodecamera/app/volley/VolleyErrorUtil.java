package com.uboss.godcodecamera.app.volley;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.uboss.godcodecamera.App;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by cuiyan on 15/12/9.
 */
public class VolleyErrorUtil {
    public static void onErrorResponse(VolleyError volleyError){

        NetworkResponse networkResponse = volleyError.networkResponse;

        if (networkResponse != null) {
            int statusCode =networkResponse.statusCode;
            Log.e("VolleyError", "statusCode=="+statusCode);

            //"401".equals(statusCode)
            if(networkResponse.data != null){

                String msg = new String(networkResponse.data);
                Log.i("VolleyError","msg=="+msg);
                try {
                    JSONObject json = new JSONObject(msg);
//                    String errid = json.get("errid").toString();
                    String errmsg = json.get("errmsg").toString();
                    Toast.makeText(App.getContext(),errmsg, Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
        if (volleyError instanceof TimeoutError) {
            Toast.makeText(App.getContext(),"请求超时！请重试！", Toast.LENGTH_LONG).show();
            Log.e("volleyError","TimeoutError 请求超时！请重试！");

        }
        if(volleyError instanceof ServerError || volleyError instanceof AuthFailureError){
//            Toast.makeText(MyApplication.getContext(),"服务器开小差啦，请稍后重试！",Toast.LENGTH_LONG).show();
            Log.e("volleyError","ServerError 服务器开小差啦，请稍后重试！");
        }
        if(volleyError instanceof NetworkError || volleyError instanceof NoConnectionError){
//            Toast.makeText(MyApplication.getContext(),"网络错误,请重试！",Toast.LENGTH_LONG).show();
            Log.e("volleyError","NetworkError 网络错误,请重试！！");

        }

        //但是volley会解释为io异常
//
//            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
//                // HTTP Status Code: 401 Unauthorized
//                Toast.makeText(getApplicationContext(),"401",Toast.LENGTH_LONG).show();
//            }




    }
}
