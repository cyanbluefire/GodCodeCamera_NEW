package com.uboss.godcodecamera;

import android.os.Environment;

/**
 * Created by sky on 2015/7/6.
 */
public class AppConstants {

    public static final String APP_DIR                    = Environment.getExternalStorageDirectory() + "/StickerCamera";
    public static final String APP_TEMP                   = APP_DIR + "/temp";
    public static final String APP_IMAGE                  = APP_DIR + "/image";

    public static final int    POST_TYPE_POI              = 1;
    public static final int    POST_TYPE_TAG              = 0;
    public static final int    POST_TYPE_DEFAULT		  = 0;


    public static final float  DEFAULT_PIXEL              = 1242;                           //按iphone6设置
    public static final String PARAM_MAX_SIZE             = "PARAM_MAX_SIZE";
    public static final String PARAM_EDIT_TEXT            = "PARAM_EDIT_TEXT";
    public static final int    ACTION_EDIT_LABEL          = 8080;
    public static final int    ACTION_EDIT_LABEL_POI      = 9090;

    public static final String FEED_INFO                  = "FEED_INFO";


    public static final int REQUEST_CROP = 6709;
    public static final int REQUEST_PICK = 9162;
    public static final int RESULT_ERROR = 404;

    /*****--cyan********/
    public static final String Main_Pic_File_Name = "hahaha";
    public static final String SP_File_MyGodCode = "my_god_code";
    public static final String DB_MyGodCode = "my_god_code.db";
    public static int DB_MyGodCode_Version = 1;

    public static final String UPYUN_PATH="http://ssobu-dev.b0.upaiyun.com/";
    public static final String UPYUN_BUCKET="ssobu-dev";
    public static final String UPYUN_BUCKET_KEY="vaQU6JGHQC8HamRHEeT9izlhHqE=";
    public static final String HOME_URL = "http://stage.godcode.ulaiber.com";
    //public static final String HOME_URL = "http://10.17.1.40:5000/";


//    public static final String UPYUN_PATH="http://ssobu.b0.upaiyun.com";
//    public static final String UPYUN_BUCKET="ssobu";
//    public static final String UPYUN_BUCKET_KEY="MU9doPbx0w2qDQdKSk/TW3LKjnw=";

    public static final String SALT_OF_DEVICE_CODE = "godcode";

}
