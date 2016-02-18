package com.uboss.godcodecamera.app.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.common.util.FileUtils;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.Bimp;
import com.uboss.godcodecamera.app.MyUtil.ImageItem;
import com.uboss.godcodecamera.app.MyUtil.PublicWay;
import com.uboss.godcodecamera.app.MyUtil.Res;
import com.uboss.godcodecamera.app.adapter.MorePicAdapter;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;
import com.uboss.godcodecamera.app.camera.util.EffectUtil;
import com.uboss.godcodecamera.app.model.Addon;
import com.uboss.godcodecamera.app.view.MyGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;

public class MakeQrcodeActivity extends AppCompatActivity {
    private static final String TAG = "MakeQrcodeActivity";
    private static final int ADD_MORE_PIC = 0x000001;//添加图片弹框时区分是主图还是配图

    ArrayList<String> arr_model_title = new ArrayList<String>();
    static ArrayList<String> arr_model_instruction = new ArrayList<String>();
    static ArrayList<Integer> addonList = new ArrayList<Integer>();
    ArrayList<HashMap<String,Object>> models = new ArrayList<>();
    SimpleAdapter model_adapter;
    public static MorePicAdapter adapter;
    private PopupWindow pop = null;
    private LinearLayout ll_popup;
    public static Bitmap bimap;
    private View parentView;



    static{
        Log.i(TAG,"addonList static");
        addonList.add(R.drawable.sticker1);
        addonList.add(R.drawable.sticker2);
        addonList.add(R.drawable.sticker3);
        addonList.add(R.drawable.sticker4);
        addonList.add(R.drawable.sticker5);
        addonList.add(R.drawable.sticker6);
        addonList.add(R.drawable.sticker7);
        addonList.add(R.drawable.sticker8);
    }
    static {
        Log.i(TAG,"arr_model_instruction static");
        arr_model_instruction.add("先文后图");
        arr_model_instruction.add("先图后文");
        arr_model_instruction.add("");
        arr_model_instruction.add("默认模板");
        arr_model_instruction.add("1");
        arr_model_instruction.add("2");
        arr_model_instruction.add("3");
        arr_model_instruction.add("4");
    }
    //工具区
    @InjectView(R.id.list_models)
    HListView bottomToolBar;    //--cyan 包括贴纸，滤镜，标签选择 HListView 是一个横向的listview类，包括在gradle中
    @InjectView(R.id.model_area)
    ViewGroup toolArea;
    @InjectView(R.id.et_qrcode_content_up)
    EditText et_qrcode_content_up;
    @InjectView(R.id.et_qrcode_content_down)
    EditText et_qrcode_content_down;
    @InjectView(R.id.qrcode_photos)
    MyGridView qrcode_photos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.mipmap.add_pic);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_make_qrcode, null);
        setContentView(R.layout.activity_make_qrcode);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        setModelData();
        pop = new PopupWindow(MakeQrcodeActivity.this);

        View view = getLayoutInflater().inflate(R.layout.item_popupwindows, null);

        ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);

        pop.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop.setBackgroundDrawable(new BitmapDrawable());
        pop.setFocusable(true);
        pop.setOutsideTouchable(true);
        pop.setContentView(view);

        RelativeLayout parent = (RelativeLayout) view.findViewById(R.id.parent);
        Button bt1 = (Button) view
                .findViewById(R.id.item_popupwindows_camera);
        Button bt2 = (Button) view
                .findViewById(R.id.item_popupwindows_Photo);
        Button bt3 = (Button) view
                .findViewById(R.id.item_popupwindows_cancel);
        parent.setOnClickListener(clickListener);
        bt1.setOnClickListener(clickListener);
        bt2.setOnClickListener(clickListener);
        bt3.setOnClickListener(clickListener);

        model_adapter= new SimpleAdapter(MakeQrcodeActivity.this,getToolList(),R.layout.item_bottom_toolbar,
                new String[]{"title","img","instruction"},
                new int[]{R.id.tv_model_title,R.id.img_model,R.id.tv_model_instruction});
        bottomToolBar.setAdapter(model_adapter);

        bottomToolBar.setOnItemClickListener( new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int position, long arg3) {
                Toast.makeText(MakeQrcodeActivity.this,position+"",Toast.LENGTH_SHORT).show();
                switch (position){
                    case 0:
                        //先文后图
                        changeModel(0,R.drawable.ic_launcher);
                        changeModel(1,addonList.get(1));
                        et_qrcode_content_down.setVisibility(View.GONE);
                        et_qrcode_content_up.setVisibility(View.VISIBLE);
                        model_adapter.notifyDataSetChanged();
                        break;
                    case 1:
                        //先图后文
                        changeModel(1,R.drawable.ic_launcher);
                        changeModel(0,addonList.get(0));
                        et_qrcode_content_up.setVisibility(View.GONE);
                        et_qrcode_content_down.setVisibility(View.VISIBLE);
                        model_adapter.notifyDataSetChanged();
                        break;
                    default:
                        break;

                }
//                labelSelector.hide();       //之前选择的贴纸隐藏
//                Addon sticker = EffectUtil.addonList.get(arg2);
//                EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
//                        new EffectUtil.StickerCallback() {
//                            @Override
//                            public void onRemoveSticker(Addon sticker) {
//                                labelSelector.hide();
//                            }
//                        });
            }
        });
        /**
         * 图片
         */
        qrcode_photos.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new MorePicAdapter(this);
        adapter.update();
        qrcode_photos.setAdapter(adapter);
        qrcode_photos.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.tempSelectBitmap.size()) {
                    popShow();
                }
//                else {
//                    openGalley(arg2);
//                }
            }
        });

    }
    /**
     * 添加图片弹框
     */
    private void popShow(){
        Log.i(TAG, "popShow()");
        ll_popup.startAnimation(AnimationUtils.loadAnimation(MakeQrcodeActivity.this, R.anim.activity_translate_in));
        pop.showAtLocation(parentView, Gravity.BOTTOM, 0, 0);
    }
    /**
     * 点击事件监听
     */
    View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.parent:
                {
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    break;
                }
                case R.id.item_popupwindows_camera:
                {
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    photo();
                    break;

                }
                case R.id.item_popupwindows_Photo:
                {
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    Intent intent = new Intent(MakeQrcodeActivity.this,
                            MyAlbumActivity.class);
                    intent.putExtra("isMainPic", false);//false表示选多张图
                    startActivity(intent);
                    overridePendingTransition(R.anim.activity_translate_in, R.anim.activity_translate_out);
                    break;

                }
                case R.id.item_popupwindows_cancel:
                {
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    break;

                }
                default:
                    break;
            }
        }
    };
    /**
     * 拍照
     */
    private void photo() {
        startActivityForResult(new Intent(MakeQrcodeActivity.this, CameraActivity.class),ADD_MORE_PIC);
    }
    /**
     * 切换模板信息
     * @param position
     * @param img_id
     */
    private void changeModel(int position,int img_id){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("title",arr_model_title.get(position));
        hashMap.put("img",img_id);
        hashMap.put("instruction",arr_model_instruction.get(position));
        models.set(position,hashMap);
    }

    private ArrayList<HashMap<String, Object>> getToolList() {
        for(int i=0;i<addonList.size();i++){
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("title",arr_model_title.get(i));
            hashMap.put("img",addonList.get(i));
            hashMap.put("instruction",arr_model_instruction.get(i));
            models.add(hashMap);
        }

        return models;
    }

    private void setModelData(){
//        Resources res = MakeQrcodeActivity.this.getResources();
//
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));
//        addonList.add(BitmapFactory.decodeResource(res,R.drawable.sticker1));


        //标题
        arr_model_title.add("图文顺序");
        arr_model_title.add("");
        arr_model_title.add("上次内容");
        arr_model_title.add("内容模板");
        Log.i(TAG,"addonList.size()=="+addonList.size());
        while (arr_model_title.size()<addonList.size()){
            arr_model_title.add("");
        }
        Log.e(TAG,"title=="+arr_model_title.size()+" addonList=="+addonList.size()+" instru=="+arr_model_instruction.size());

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_MORE_PIC:{
                Log.i(TAG,"ADD_MORE_PIC");
                if(data == null||"".equals(data)){
                    Log.i(TAG,"Image is null");
                    return;
                }
                if (Bimp.tempSelectBitmap.size() < 9 && resultCode == RESULT_OK) {

                    String fileName = String.valueOf(System.currentTimeMillis());
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    FileUtils.saveBitmap(bm, fileName);
                    Log.i(TAG,"img filename"+fileName);
                    ImageItem takePhoto = new ImageItem();
                    takePhoto.setBitmap(bm);
                    Bimp.tempSelectBitmap.add(takePhoto);
                }
                break;
            }
            default:
                break;
        }
    }
}
