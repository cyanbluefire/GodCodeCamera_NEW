package com.uboss.godcodecamera.app.ui;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TestActivity extends AppCompatActivity {
    @InjectView(R.id.img_test)
    ImageView img_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.inject(this);

        Uri.decode(CameraActivity.Main_Photo_Name);
    }
}
