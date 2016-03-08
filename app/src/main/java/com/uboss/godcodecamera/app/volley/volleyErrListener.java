package com.uboss.godcodecamera.app.volley;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.uboss.godcodecamera.App;

/**
 * Created by cyan on 2015/8/25.
 */
public class volleyErrListener implements Response.ErrorListener {

    Context mContext = App.getContext();
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Toast.makeText(mContext,
                VolleyErrorHelper.getMessage(volleyError, mContext),
                Toast.LENGTH_LONG).show();
    }
}
