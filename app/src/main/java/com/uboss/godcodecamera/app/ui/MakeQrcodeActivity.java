package com.uboss.godcodecamera.app.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;
import com.uboss.godcodecamera.app.camera.util.EffectUtil;
import com.uboss.godcodecamera.app.model.Addon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;

public class MakeQrcodeActivity extends AppCompatActivity {
    private static final String TAG = "MakeQrcodeActivity";
    ArrayList<String> arr_model_title = new ArrayList<String>();
    static ArrayList<String> arr_model_instruction = new ArrayList<String>();
    static ArrayList<Integer> addonList = new ArrayList<Integer>();
    ArrayList<HashMap<String,Object>> models = new ArrayList<>();
    SimpleAdapter model_adapter;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_qrcode);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        setModelData();
        if(bottomToolBar == null){
            Log.i(TAG,"bottomToolBar == null");
        }
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

//                        HashMap<String, Object> map = new HashMap<String, Object>();
//                        map.put("title",arr_model_title.get(1));
//                        map.put("img",R.drawable.ic_launcher);
//                        map.put("instruction",arr_model_instruction.get(1));
//                        models.set(1,map);
//
//                        map.put("title",arr_model_title.get(0));
//                        map.put("img",addonList.get(0));
//                        map.put("instruction",arr_model_instruction.get(0));
//                        models.set(0,map);
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
    }

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

}
