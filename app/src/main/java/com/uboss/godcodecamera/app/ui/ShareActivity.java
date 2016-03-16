package com.uboss.godcodecamera.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.common.util.FileUtils;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.QRCodeUtil;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShareActivity extends AppCompatActivity {
    private Context context;

    private static final String TAG = "ShareActivity";
    @InjectView(R.id.img_test)
    ImageView img_test;

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.inject(this);

        context = ShareActivity.this;
//        img_test.setImageBitmap(BitmapFactory.decodeFile(file_path));
        img_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share("/storage/emulated/0/Godcode/GodCode/20160316174647.jpg");
            }
        });
    }

    /**
     * 分享
     *
     */
    private void share(String filename){
        Log.e(TAG,"sharePicture() start");
        Log.i(TAG,"filename=="+filename);

        UMImage image = new UMImage(ShareActivity.this, BitmapFactory.decodeFile(filename));


//        new ShareAction(this).setDisplayList( displaylist )
//                .setShareboardclickCallback(shareBoardlistener)
//                .open()
//        ;



        new ShareAction(ShareActivity.this).setDisplayList( displaylist )
                .withMedia(image)
                .setCallback(umShareListener)
                .open();

    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.i("UMShareListener","onResult()");
            Toast.makeText(context, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Log.i("UMShareListener","onError()");
            Toast.makeText(context,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Log.i("UMShareListener","onCancel()");
            Toast.makeText(context,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult");
//        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);

        //Umeng分享
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);

        //添加文字回调
//        if (AppConstants.ACTION_EDIT_LABEL== requestCode && data != null) {
//            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
//            if(StringUtils.isNotEmpty(text)){
//                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,text);
//                addLabel(tagItem);
//            }
//        }else if(AppConstants.ACTION_EDIT_LABEL_POI== requestCode && data != null){
//            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
//            if(StringUtils.isNotEmpty(text)){
//                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_POI,text);
//                addLabel(tagItem);
//            }
//        }
    }
}
