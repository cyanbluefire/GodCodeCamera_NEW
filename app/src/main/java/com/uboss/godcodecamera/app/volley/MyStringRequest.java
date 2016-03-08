package com.uboss.godcodecamera.app.volley;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by cuiyan on 15/12/7.
 *
 * add getStatusCode()
 */
public class MyStringRequest extends StringRequest {

    private int mStatusCode;

    public MyStringRequest(int method, String url, Response.Listener<String> listener,
                           Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public int getStatusCode() {
        return mStatusCode;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        mStatusCode = response.statusCode;
        return super.parseNetworkResponse(response);
    }
}

