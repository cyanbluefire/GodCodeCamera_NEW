package com.uboss.godcodecamera.app.ui;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.common.util.FileUtils;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.QRCodeUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ShareActivity extends AppCompatActivity {

    @InjectView(R.id.img_test)
    ImageView img_test;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.inject(this);
        String file_path=FileUtils.getQrcodePath();
        Log.i("cyan","qrcode file_path"+file_path);
        if(QRCodeUtil.createQRImage("hello",200,200,null, file_path)){
            Log.i("cyan","create qrcode success");
        }else {
            Log.e("cyan","create qrcode failed");
        }
        img_test.setImageBitmap(BitmapFactory.decodeFile(file_path));
    }
}
