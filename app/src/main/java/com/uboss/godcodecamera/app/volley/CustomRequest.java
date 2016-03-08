package com.uboss.godcodecamera.app.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import java.io.UnsupportedEncodingException;
//import com.android.volley.Response.Listener;

/**
 * Created by cyan on 2015/8/25.
 */
public class CustomRequest extends Request<String> {

    private final Listener<String> mListener;
    private String mUserName;

    public CustomRequest(int method, String url, Listener<String> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;
    }
    public CustomRequest(String url, String userName, Listener<String> listener, ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mUserName = userName;
        mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse networkResponse) {
        String parsed;
        try {
            parsed = new String(networkResponse.data, HttpHeaderParser.parseCharset(networkResponse.headers));
        }catch(UnsupportedEncodingException e){
            parsed = new String(networkResponse.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(networkResponse));
    }

    //传递响应，通过我们初始化时候设置的成功监听器来传递HTTP响应结果。
    @Override
    protected void deliverResponse(String s) {
    mListener.onResponse(s);
    }

    public byte[] getBody(){
        try {
            return mUserName == null ? null:mUserName.getBytes("utf-8");
        }catch (UnsupportedEncodingException uee){
            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", mUserName, "utf-8");
            return null;
        }
    }
}
