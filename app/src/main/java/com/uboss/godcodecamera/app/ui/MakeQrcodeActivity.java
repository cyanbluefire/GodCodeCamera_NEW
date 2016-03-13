package com.uboss.godcodecamera.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.common.util.FileUtils;
import com.common.util.MD5Util;
import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.Bimp;
import com.uboss.godcodecamera.app.MyUtil.ImageItem;
import com.uboss.godcodecamera.app.MyUtil.MyFileUtils;
import com.uboss.godcodecamera.app.MyUtil.PublicWay;
import com.uboss.godcodecamera.app.adapter.MorePicAdapter;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;
import com.uboss.godcodecamera.app.camera.ui.PhotoProcessActivity;
import com.uboss.godcodecamera.app.view.MyGridView;
import com.uboss.godcodecamera.base.GodeCode;
import com.upyun.block.api.listener.CompleteListener;
import com.upyun.block.api.listener.ProgressListener;
import com.upyun.block.api.main.UploaderManager;
import com.upyun.block.api.utils.UpYunUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;

/**
 * 本页需要重构，增加model类--cyan*
 */
public class MakeQrcodeActivity extends AppCompatActivity {
    private static final String TAG = "MakeQrcodeActivity";
    private static final int ADD_MORE_PIC = 0x000001;//添加图片弹框时区分是主图还是配图
    private static final int SHOP_LOCATION = 0x000002;//百度地图定位周边商家

    ArrayList<String> arr_model_title = new ArrayList<String>();
    static ArrayList<String> arr_model_instruction = new ArrayList<String>();
    static ArrayList<Integer> addonList = new ArrayList<Integer>();
    static ArrayList<Integer> addonList_focused = new ArrayList<Integer>();

    static ArrayList<Integer> arr_model_preview = new ArrayList<Integer>();
    ArrayList<HashMap<String,Object>> models = new ArrayList<>();

    SimpleAdapter model_adapter;
    public static MorePicAdapter adapter;
    private PopupWindow pop = null;
    private PopupWindow pop_preview_photo;
    private LinearLayout ll_popup;
    private RelativeLayout rl_popup_preview;
    private ImageView img_preview;
    public static Bitmap bimap;
    private View parentView;
    private static boolean hasqrcode = false;
    private boolean is_up_edittext = true;
    private String url = "";
    private int current_model = 0;
    private int last_model ;
    private int use_model =1;      //最终传给后台的选中模板顺序
    private int max_num = 1;        //每个模板最多可选的照片数
//    private boolean lastisDefaultModel = true;

    //暂存数据
    private static String content_text;
    private static boolean istextBeforePhoto = true;
    //preview
    private static String shop_name ;
    private static String uid;
    private static String city ;
    private String code;    //识别设备IMEI
    private String platform = "android";

    //upyun
    public static ArrayList<String> arr_path =  new ArrayList<String>();//要上传到upyun上得图片路径
    public int path_count = 0;
    private ArrayList arr_upyun_path = new ArrayList();


    //未选中模板图
    static{
        Log.i(TAG,"addonList static");
        addonList.add(R.mipmap.picture_icon1);
        addonList.add(R.mipmap.picture_icon2);
        addonList.add(R.mipmap.circle);
        if(hasqrcode){
            addonList.add(R.mipmap.qrcode);
            addonList.add(R.mipmap.circle);
        }
        addonList.add(R.mipmap.default_model);
        addonList.add(R.mipmap.model1_1);
        addonList.add(R.mipmap.model2_1);
        addonList.add(R.mipmap.model3_1);
        addonList.add(R.mipmap.model4_1);
        addonList.add(R.mipmap.model5_1);
        addonList.add(R.mipmap.model6_1);

    }

    //选中的模板图
    static {
        addonList_focused.add(R.mipmap.picture_icon1_focused);
        addonList_focused.add(R.mipmap.picture_icon2_focused);
        addonList_focused.add(R.mipmap.circle);
        if(hasqrcode){
            addonList_focused.add(R.mipmap.qrcode);
            addonList_focused.add(R.mipmap.circle);
        }
        addonList_focused.add(R.mipmap.default_model_focused);
        addonList_focused.add(R.mipmap.model1_1focused);
        addonList_focused.add(R.mipmap.model2_1focused);
        addonList_focused.add(R.mipmap.model3_1focused);
        addonList_focused.add(R.mipmap.model4_1focused);
        addonList_focused.add(R.mipmap.model6_1focused);
        addonList_focused.add(R.mipmap.model5_1focused);


    }
    static {
        arr_model_preview.add(0);
        arr_model_preview.add(0);
        arr_model_preview.add(0);
        arr_model_preview.add(0);
        if(hasqrcode){
            arr_model_preview.add(0);
            arr_model_preview.add(0);
        }

        arr_model_preview.add(R.mipmap.model1);
        arr_model_preview.add(R.mipmap.model2);
        arr_model_preview.add(R.mipmap.model3);
        arr_model_preview.add(R.mipmap.model4);
        arr_model_preview.add(R.mipmap.model6);
        arr_model_preview.add(R.mipmap.model5);


    }

    static {
        Log.i(TAG,"arr_model_instruction static");
        arr_model_instruction.add("先文后图");
        arr_model_instruction.add("先图后文");
        arr_model_instruction.add("");
        if(hasqrcode){
            arr_model_instruction.add("");
            arr_model_instruction.add("");
        }
        arr_model_instruction.add("默认模板");
        arr_model_instruction.add("神码日报");
        arr_model_instruction.add("时尚杂志");
        arr_model_instruction.add("热血动漫");
        arr_model_instruction.add("激怒我了");
        arr_model_instruction.add("可爱动物");
        arr_model_instruction.add("心情格调");


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
    @InjectView(R.id.img_title_right)
    Button btn_title_right;
    @InjectView(R.id.img_title_left)
    ImageView img_title_left;
    @InjectView(R.id.rl_location)
    RelativeLayout rl_location;
    @InjectView(R.id.tv_location)
    TextView tv_location;
    @InjectView(R.id.btn_create_qrcode)
    Button btn_create_qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Res.init(this);
        bimap = BitmapFactory.decodeResource(
                getResources(),
                R.mipmap.plus_icon);
        PublicWay.activityList.add(this);
        parentView = getLayoutInflater().inflate(R.layout.activity_make_qrcode, null);
        setContentView(R.layout.activity_make_qrcode);
        ButterKnife.inject(this);

        //关闭自动弹出键盘
        getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        init();
        Log.e(TAG,"CameraActivity.Main_Photo_Name::"+CameraActivity.Main_Photo_Name);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"istextBeforePhoto=="+istextBeforePhoto);
        if(istextBeforePhoto){
            et_qrcode_content_down.setVisibility(View.GONE);
            et_qrcode_content_up.setVisibility(View.VISIBLE);
            if(content_text != null)
                et_qrcode_content_up.setText(content_text);
        }else {
            et_qrcode_content_up.setVisibility(View.GONE);
            et_qrcode_content_down.setVisibility(View.VISIBLE);
            if(content_text != null)
                et_qrcode_content_down.setText(content_text);
        }
        if(shop_name != null){
            tv_location.setText(shop_name);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,CameraActivity.class));
    }

    private void init() {

        setModelData();
        initPopupAddPhoto();
        initPopupPreview();
        rl_location.setOnClickListener(clickListener);
        btn_create_qrcode.setOnClickListener(clickListener);

        model_adapter= new SimpleAdapter(MakeQrcodeActivity.this,getToolList(),R.layout.item_bottom_toolbar,
                new String[]{"title","img","instruction"},
                new int[]{R.id.tv_model_title,R.id.img_model,R.id.tv_model_instruction});
        bottomToolBar.setAdapter(model_adapter);

        //默认是空白模板为选中状态
        changeModel(0,R.mipmap.picture_icon1_focused);
        if(hasqrcode){
            last_model = 5;
            current_model = 5;
            changeModel(5,R.mipmap.default_model_focused);
        }
        else{
            changeModel(3,R.mipmap.default_model_focused);
            last_model = 3;
            current_model = 3;
        }
        model_adapter.notifyDataSetChanged();


        bottomToolBar.setOnItemClickListener( new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int position, long arg3) {
                Toast.makeText(MakeQrcodeActivity.this,position+"",Toast.LENGTH_SHORT).show();
                if(hasqrcode){
                    switch (position){
                        case 0:
                            textBeforePhoto();
                            break;
                        case 1:
                            //先图后文
                            changeModel(1,R.mipmap.picture_icon2_focused);
                            changeModel(0,addonList.get(0));
                            et_qrcode_content_up.setVisibility(View.GONE);
                            et_qrcode_content_down.setVisibility(View.VISIBLE);
                            is_up_edittext = false;
                            model_adapter.notifyDataSetChanged();
                            use_model = 2;

                            istextBeforePhoto = false;
                            Log.e(TAG,"istextBeforePhoto=="+istextBeforePhoto);
                            break;
                        case 2:
                            break;
                        case 3:
                            Toast.makeText(MakeQrcodeActivity.this,"上次内容!",Toast.LENGTH_SHORT).show();

                            break;
                        case 4:
                            break;
                        case 5:
                            current_model = position;
                            clickDefaultModel();
                            break;
                        case 6:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 7:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 8:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 9:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 10:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 11:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        default:
                            break;

                    }
                }else{
                    switch (position){
                        case 0:
                            textBeforePhoto();
                            break;
                        case 1:
                            //先图后文
                            changeModel(1,R.mipmap.picture_icon2_focused);
                            changeModel(0,addonList.get(0));
                            et_qrcode_content_up.setVisibility(View.GONE);
                            et_qrcode_content_down.setVisibility(View.VISIBLE);
                            is_up_edittext = false;
                            model_adapter.notifyDataSetChanged();
                            use_model = 2;

                            istextBeforePhoto = false;
                            Log.e(TAG,"istextBeforePhoto=="+istextBeforePhoto);
                            break;
                        case 2:
                            break;
                        case 3:
                            current_model = position;
                            clickDefaultModel();
                            break;
                        case 4:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 5:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 6:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 7:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 8:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        case 9:
                            img_preview.setImageResource(arr_model_preview.get(position));
                            popPreview();
                            current_model = position;
                            max_num = 2;
                            break;
                        default:
                            break;

                    }
                }

//                if(hasqrcode){
//                    if(position>2){
//                        if(position == 3){
//                            Toast.makeText(MakeQrcodeActivity.this,"上次内容!",Toast.LENGTH_SHORT).show();
//
//                        }
//                        //5是空白模板，6以后都是模板了
//                        if(position == 5){
//                            changeModel(position,R.mipmap.default_model_focused);
//                            current_model = position;
//                            model_adapter.notifyDataSetChanged();
//                            lastisDefaultModel = true;
//                        }
//                        if(position >5){
//                            if(lastisDefaultModel){
//                                changeModel(5,R.mipmap.default_model);
//                                lastisDefaultModel = false;
//                            }
//                            img_preview.setImageResource(arr_model_preview.get(position));
//                            popPreview();
//                            current_model = position;
//                        }
//
//                    }
//                }else {
//                    //3是空白模板，4以后就是模板
//                    //
//                    if(position == 3){
//                        changeModel(position,R.mipmap.default_model_focused);
//                        model_adapter.notifyDataSetChanged();
//                        current_model = position;
//                        lastisDefaultModel = true;
//                    }
//                    if(position >3){
//                        if(lastisDefaultModel){
//                            changeModel(3,R.mipmap.default_model);
//                            lastisDefaultModel = false;
//                        }
//                        img_preview.setImageResource(arr_model_preview.get(position));
//                        popPreview();
//                        current_model = position;
//                    }
//                }
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
        //预览
//        btn_title_right.setOnClickListener(clickListener);
        btn_title_right.setVisibility(View.GONE);
        img_title_left.setOnClickListener(clickListener);
    }

    private void clickDefaultModel(){
        changeModel(last_model,addonList.get(last_model));
        changeModel(current_model,addonList_focused.get(current_model));
        model_adapter.notifyDataSetChanged();
        last_model = current_model;
        if(hasqrcode){
            use_model = current_model - 5;
        }else{
            use_model = current_model - 3;
        }
        Toast.makeText(MakeQrcodeActivity.this,"使用默认模板",Toast.LENGTH_SHORT).show();
        textBeforePhoto();
    }
    private void textBeforePhoto(){
        //先文后图
        changeModel(0,R.mipmap.picture_icon1_focused);
        changeModel(1,addonList.get(1));
        et_qrcode_content_down.setVisibility(View.GONE);
        et_qrcode_content_up.setVisibility(View.VISIBLE);
        is_up_edittext = true;
        model_adapter.notifyDataSetChanged();
        use_model = 1;
        istextBeforePhoto = true;
    }
    private void initPopupPreview() {
        pop_preview_photo = new PopupWindow(MakeQrcodeActivity.this);
        View view = getLayoutInflater().inflate(R.layout.item_popup_preview,null);
        pop_preview_photo.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        pop_preview_photo.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        pop_preview_photo.setBackgroundDrawable(new BitmapDrawable());
        pop_preview_photo.setFocusable(true);
        pop_preview_photo.setOutsideTouchable(true);
        pop_preview_photo.setContentView(view);

        rl_popup_preview = (RelativeLayout)view.findViewById(R.id.rl_popup_preview);
        img_preview = (ImageView)view.findViewById(R.id.img_preview);
        ImageView btn_cancle_preview = (ImageView)view.findViewById(R.id.btn_cancle_preview);
        btn_cancle_preview.setOnClickListener(clickListener);
        Button btn_use_modle = (Button)view.findViewById(R.id.btn_use_modle);
        btn_use_modle.setOnClickListener(clickListener);


    }

    private void initPopupAddPhoto() {
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
     * 模板预览弹框
     */
    private void popPreview(){
        Log.i(TAG, "popPreview()");
        rl_popup_preview.startAnimation(AnimationUtils.loadAnimation(MakeQrcodeActivity.this, R.anim.activity_translate_in));
        pop_preview_photo.showAtLocation(parentView, Gravity.TOP, 0, 0);
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
                    content_text = getText();           //已输入文字暂存

                    photo();
                    break;

                }
                case R.id.item_popupwindows_Photo:
                {
                    pop.dismiss();
                    ll_popup.clearAnimation();
                    content_text = getText();           //已输入文字暂存

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
                case R.id.img_title_left:
                    startActivity(new Intent(MakeQrcodeActivity.this, CameraActivity.class));
                    finish();
                    break;
                case R.id.btn_cancle_preview:
                    pop_preview_photo.dismiss();
                    break;
                case R.id.rl_location:
                    content_text = getText();           //已输入文字暂存

                    Intent intent = new Intent(MakeQrcodeActivity.this, ShopLocationActivity.class);
                    startActivityForResult(intent,SHOP_LOCATION);
                    break;
                case R.id.btn_use_modle:
                    pop_preview_photo.dismiss();
                    changeModel(last_model,addonList.get(last_model));
                    changeModel(current_model,addonList_focused.get(current_model));
                    model_adapter.notifyDataSetChanged();
                    last_model = current_model;
                    if(hasqrcode){
                        use_model = current_model - 5;
                    }else{
                        use_model = current_model - 3;
                    }
                    use_model = use_model +2 ;          //默认的两个模板放到了1，2
                    Toast.makeText(MakeQrcodeActivity.this,"使用模板"+use_model,Toast.LENGTH_SHORT).show();
//                    lastisDefaultModel = false;
                    break;
                case R.id.btn_create_qrcode:
                    upYunPhoto();
//                    startActivity(new Intent(MakeQrcodeActivity.this, PhotoProcessActivity.class));


//                    Intent intent_process =  new Intent(MakeQrcodeActivity.this, PhotoProcessActivity.class);
//                    //文字描述
//                    if (is_up_edittext)
//                        intent_process.putExtra("content",et_qrcode_content_up.getText().toString());
//                    else
//                        intent_process.putExtra("content",et_qrcode_content_down.getText().toString());
//                    //包含图片张数
//                    intent_process.putExtra("count",Bimp.tempSelectBitmap.size());
//                    //二维码内容地址
//                    intent_process.putExtra("url",url);
//                    startActivity(intent_process);
                    break;

                default:
                    break;
            }
        }
    };

    /**
     * upyun上传图片
     */
    private void upLoadPicture() {
        Log.i(TAG,"upLoadMorePic()");
        if(Bimp.tempSelectBitmap.size() < 1){
            Log.i(TAG,"arr_path size 0");
            AlertDialog.Builder builder = new AlertDialog.Builder(MakeQrcodeActivity.this);
            builder.setTitle("未添加图片").setIcon(android.R.drawable.ic_dialog_info)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            builder.show();
            return;
        }
        for(int i=0;i<Bimp.tempSelectBitmap.size();i++){
            Log.i(TAG,"photo size=="+Bimp.tempSelectBitmap.size());
            arr_path.add(i, MyFileUtils.saveTempBitmap(Bimp.tempSelectBitmap.get(i).getBitmap(), "morePic" + (i)));
        }


        if(path_count <arr_path.size())
            new UpyunUpload().execute(arr_path.get(path_count));

        for(String s:arr_path){
            Log.i(TAG,"arr_path::"+s);
        }
    }
    public class UpyunUpload extends AsyncTask<String, Void, String> {
        // 空间名
//        String bucket = "ssobu-dev";
        // 表单密钥
//        String bucket_key = "vaQU6JGHQC8HamRHEeT9izlhHqE=";

        String savePath = "/asset_img/avatar/{filemd5}{.suffix}.jpeg";

        public UpyunUpload(){
//            http://ssobu-dev.b0.upaiyun.com/asset_img/avatar/70ccd7325b5f8c9e2484e041b9c21ddd.jpeg
        }

        @Override
        protected String doInBackground(String...localpath) {
            File localFile = new File(localpath[0]);
            try {
				/*
				 * 设置进度条回掉函数
				 *
				 * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
				 * 为了解决这个问题，代码会判断如果实际传送的大小大于图片
				 * ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
				 */
                ProgressListener progressListener = new ProgressListener() {
                    @Override
                    public void transferred(long transferedBytes, long totalBytes) {
                        // do something...
                        System.out.println("trans:" + transferedBytes + "; total:" + totalBytes);
                    }
                };

                CompleteListener completeListener = new CompleteListener() {
                    @Override
                    public void result(boolean isComplete, String result, String error) {
                        // do something...

                        if (isComplete) {
//                Toast.makeText(MyApplication.getContext(), "第"+(path_count+1)+"张上传成功", Toast.LENGTH_LONG).show();
                            Log.i(TAG,"第"+(path_count+1)+"张上传成功");

                            try {
                                JSONObject json_result = new JSONObject(result);
                                JSONObject json_args = (JSONObject)json_result.get("args");
                                String path = json_args.get("path").toString();
                                Log.i(TAG,"path=="+path);
                                    //附图路径
                                    path= AppConstants.UPYUN_PATH+path;
                                    Log.i(TAG,"path=="+path);

                                    arr_upyun_path.add(path);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            path_count++;
                            if(path_count < arr_path.size()){
                                new UpyunUpload().execute(arr_path.get(path_count));
                            }else{
                                Toast.makeText(App.getContext(), "所有图片上传完成", Toast.LENGTH_LONG).show();
                                Log.i(TAG,"所有图片上传完成");

                                //
                                preview();
                            }


                        } else {
                            Toast.makeText(App.getContext(), "网络问题，上传失败，点击重新上传!", Toast.LENGTH_LONG).show();
                            Log.i(TAG,"第"+(path_count+1)+"张上传失败");

                        }


                        System.out.println("isComplete:"+isComplete+";result:"+result+";error:"+error);


                    }
                };

                UploaderManager uploaderManager = UploaderManager.getInstance(AppConstants.UPYUN_BUCKET);
                uploaderManager.setConnectTimeout(60);
                uploaderManager.setResponseTimeout(60);
                Map<String, Object> paramsMap = uploaderManager.fetchFileInfoDictionaryWith(localFile, savePath);
                //还可以加上其他的额外处理参数...
                paramsMap.put("return_url", "http://httpbin.org/get");
                // signature & policy 建议从服务端获取
                String policyForInitial = UpYunUtils.getPolicy(paramsMap);
                String signatureForInitial = UpYunUtils.getSignature(paramsMap, AppConstants.UPYUN_BUCKET_KEY);
                uploaderManager.upload(policyForInitial, signatureForInitial, localFile, progressListener, completeListener);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "result";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i(TAG, "getResult()");

        }
    }
    private void preview() {
        StringBuilder sb = new StringBuilder();
        sb.append(code).append(platform).append(AppConstants.SALT_OF_DEVICE_CODE);
        String black_code = MD5Util.MyMD5(sb.toString());
        Log.i(TAG,"path::"+arr_upyun_path.toString());
        JSONObject json = new JSONObject();
        JSONObject json_article_content = new JSONObject();
//        String article_content="";
        try {
//            if(et_qrcode_content_up.getVisibility() == View.VISIBLE){
//                json_article_content.put("text",MyFileUtils.string2Unicode(et_qrcode_content_up.getText().toString()));
//
//            }else{
//                json_article_content.put("text",MyFileUtils.string2Unicode(et_qrcode_content_down.getText().toString()));
//            }
            json_article_content.put("text",MyFileUtils.string2Unicode(getText()));
            json_article_content.put("images",new JSONArray(arr_upyun_path));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String article_content = json_article_content.toString();
//        Log.i(TAG,"before::"+article_content);
        article_content = article_content.replace("\\\\","\\");
//        Log.i(TAG,"after::"+article_content);
//        Log.i(TAG,"use_model=="+use_model);
        Intent intent = new Intent(MakeQrcodeActivity.this,GodCodeWebActivity.class);
        intent.putExtra("code",code);
        intent.putExtra("platform",platform);
        intent.putExtra("black_code",black_code);
        intent.putExtra("article_content",article_content);
        intent.putExtra("poi_uid",uid);
        intent.putExtra("poi_city",MyFileUtils.string2Unicode(city));
        intent.putExtra("poi_name",MyFileUtils.string2Unicode(shop_name));
        intent.putExtra("use_model",use_model);

        startActivity(intent);
    }

    /**
     * 获得文本
     */
    private String getText(){
        if(et_qrcode_content_up.getVisibility() == View.VISIBLE){

            return et_qrcode_content_up.getText().toString();
        }else{
            return et_qrcode_content_down.getText().toString();
        }
    }

    /**
     * 设置文本
     * @param text
     */
//    private void setText(String text){
//        if(istextBeforePhoto){
//            et_qrcode_content_up.setVisibility(View.VISIBLE);
//            et_qrcode_content_down.setVisibility(View.GONE);
//            et_qrcode_content_up.setText(text);
//        }else {
//            et_qrcode_content_down.setVisibility(View.VISIBLE);
//            et_qrcode_content_up.setVisibility(View.GONE);
//            et_qrcode_content_down.setText(text);
//        }
//
//    }
    /**
     * 预览
     */
    private void upYunPhoto() {
        Log.i(TAG,"previewModel()");
        Toast.makeText(MakeQrcodeActivity.this,"预览模板 "+use_model,Toast.LENGTH_SHORT).show();
        code = App.getIMEI();
        if(tv_location.getText().toString().equals("当前地点")){
            Toast.makeText(MakeQrcodeActivity.this,"请先选择当前位置",Toast.LENGTH_SHORT).show();
            return;
        }
        upLoadPicture();
    }

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

    private void changeModel(int position,int img_id,int max_num){
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("title",arr_model_title.get(position));
        hashMap.put("img",img_id);
        hashMap.put("instruction",arr_model_instruction.get(position));
        models.set(position,hashMap);

        this.max_num = max_num;

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

        //标题
        arr_model_title.add("图文顺序");
        arr_model_title.add("");
        arr_model_title.add("");
        if(hasqrcode){
            arr_model_title.add("上次内容");
            arr_model_title.add("");
        }
        arr_model_title.add("内容模板");
        Log.i(TAG,"addonList.size()=="+addonList.size());
        while (arr_model_title.size()<addonList.size()){
            arr_model_title.add("");
        }
        Log.e(TAG,"title=="+arr_model_title.size()+" addonList=="+addonList.size()+" instru=="+arr_model_instruction.size());

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult");
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
            case SHOP_LOCATION:
                Log.e(TAG,"SHOP_LOCATION");
                shop_name = data.getStringExtra("shop_name");
                uid = data.getStringExtra("uid");
                city = data.getStringExtra("city");

                Log.i(TAG,"shop_name=="+shop_name+" uid=="+uid+" city=="+city);
                tv_location.setText(shop_name);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
