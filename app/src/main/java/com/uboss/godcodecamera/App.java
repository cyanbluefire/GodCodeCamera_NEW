package com.uboss.godcodecamera;

import android.app.Application;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.socialize.PlatformConfig;

/**
 * Created by sky on 2015/7/6.
 */
public class App extends Application {

    private static final String TAG = "App";
    protected static App       mInstance;
    private DisplayMetrics     displayMetrics = null;
    private static Context mContext;
    private  static String IMEI = "";
    public static RequestQueue requestQueue;


    public App(){
        mInstance = this;
    }

    public static App getApp() {
        if (mInstance != null && mInstance instanceof App) {
            return (App) mInstance;
        } else {
            mInstance = new App();
            mInstance.onCreate();
            return (App) mInstance;
        }
    }
    public static Context getContext(){
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initImageLoader();
        mInstance = this;
        mContext = this.getApplicationContext();

        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        IMEI = tm.getDeviceId().toString();

        requestQueue = Volley.newRequestQueue(mContext);
        intiUmeng();

    }
    private void intiUmeng() {
        Log.i("MyApplication","initUmeng()");
        PlatformConfig.setQQZone("1105253294", "1I7iT0SZ6h3K1E4g");
        PlatformConfig.setSinaWeibo("985762084","f3f6287d9a00bda69106eb6ce81d4ca1");
        PlatformConfig.setWeixin("wxb146b2c2b49df354", "bf9c2f73c62230e2baea9a5d571b736f");
    }

    public static void startVolley(Request req) {

        // 设置超时时间
        // req.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        // 将请求加入队列
        requestQueue.add(req);
        // 开始发起请求
        requestQueue.start();
    }

    public static String getIMEI(){
        Log.i(TAG,"IMEI=="+IMEI);
        return IMEI;
    }


    private void initImageLoader() {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisk(true)
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .defaultDisplayImageOptions(defaultOptions)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCache(new UnlimitedDiskCache(StorageUtils.getOwnCacheDirectory(this, AppConstants.APP_IMAGE)))
                .diskCacheSize(100 * 1024 * 1024).tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024)
                .threadPoolSize(3)
                .build();
        ImageLoader.getInstance().init(config);
    }


    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f)
    {
        return (int)(0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }



}
