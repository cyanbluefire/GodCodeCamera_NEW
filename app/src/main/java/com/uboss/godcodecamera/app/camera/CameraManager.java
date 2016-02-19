package com.uboss.godcodecamera.app.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.common.util.ImageUtils;
import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.app.MyUtil.LocalDataUtil;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;
import com.uboss.godcodecamera.app.camera.ui.CropPhotoActivity;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;
import com.uboss.godcodecamera.app.model.PhotoItem;
import com.uboss.godcodecamera.app.ui.MakeQrcodeActivity;

import java.util.Stack;

/**
 * 相机管理类
 * Created by sky on 15/7/6.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class CameraManager {

    private static final String TAG = "CameraManager";
    private static CameraManager mInstance;
    private Stack<Activity> cameras = new Stack<Activity>();

    public static CameraManager getInst() {
        if (mInstance == null) {
            synchronized (CameraManager.class) {
                if (mInstance == null)
                    mInstance = new CameraManager();
            }
        }
        return mInstance;
    }

    //打开照相界面
    public void openCamera(Context context) {
        Intent intent = new Intent(context, CameraActivity.class);
        context.startActivity(intent);
    }

    //判断图片是否需要裁剪
    public void processPhotoItem(Activity activity, PhotoItem photo,String dataName) {
        Uri uri = photo.getImageUri().startsWith("file:") ? Uri.parse(photo
                .getImageUri()) : Uri.parse("file://" + photo.getImageUri());
        if (ImageUtils.isSquare(photo.getImageUri())) {
            Log.i(TAG,"不需要裁切");
            LocalDataUtil.SaveSharedPre("mainPic",dataName,"main_pictures");
            Intent newIntent = new Intent(activity, MakeQrcodeActivity.class);

//            Intent newIntent = new Intent(activity, PhotoProcessActivity.class);
//            newIntent.setData(uri);
            activity.startActivity(newIntent);
        } else {
            Log.i(TAG,"需要裁切");
            Intent i = new Intent(activity, CropPhotoActivity.class);
            i.setData(uri);
            //TODO稍后添加
            activity.startActivityForResult(i, AppConstants.REQUEST_CROP);
        }
    }

    public void close() {
        for (Activity act : cameras) {
            try {
                act.finish();
            } catch (Exception e) {

            }
        }
        cameras.clear();
    }

    public void addActivity(Activity act) {
        cameras.add(act);
    }

    public void removeActivity(Activity act) {
        cameras.remove(act);
    }



}
