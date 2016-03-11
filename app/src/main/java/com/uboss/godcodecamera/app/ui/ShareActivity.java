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

//        img_test.setImageBitmap(BitmapFactory.decodeFile(file_path));
    }
}
