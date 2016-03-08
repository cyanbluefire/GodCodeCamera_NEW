package com.uboss.godcodecamera.app.volley;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by cyan on 2015/8/25.
 */
public class VolleyQueue {
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;

    private  VolleyQueue(){

    }
    /**
     * 初始化我们的请求队列。这个地方有一个BitmapLruCache，这个是在后面做图片加载的时候会提到的图片缓存策略
     *
     * @param context
     */
    public static void init(Context context){
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass = ((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int cacheSize = 1024 * 1024 * memClass / 8;
        mImageLoader = new ImageLoader(mRequestQueue,new BitmapLruCache(cacheSize));
    }
    public static RequestQueue getRequestQueue(){
        if(mRequestQueue != null){
            return mRequestQueue;
        }else{
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }


    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }


}
