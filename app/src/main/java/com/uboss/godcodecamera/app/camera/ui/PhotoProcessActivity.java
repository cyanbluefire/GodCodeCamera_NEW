package com.uboss.godcodecamera.app.camera.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.common.util.FileUtils;
import com.common.util.ImageUtils;
import com.common.util.StringUtils;
import com.common.util.TimeUtils;
import com.customview.LabelSelector;
import com.customview.LabelView;
import com.customview.MyHighlightView;
import com.customview.MyImageViewDrawableOverlay;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.app.Database.DBManager;
import com.uboss.godcodecamera.app.Database.DatabaseHelper;
import com.uboss.godcodecamera.app.MyUtil.Bimp;
import com.uboss.godcodecamera.app.MyUtil.QRCodeUtil;
import com.uboss.godcodecamera.app.camera.CameraBaseActivity;
import com.uboss.godcodecamera.app.camera.CameraManager;
import com.uboss.godcodecamera.app.camera.EffectService;
import com.uboss.godcodecamera.app.camera.adapter.FilterAdapter;
import com.uboss.godcodecamera.app.camera.effect.FilterEffect;
import com.uboss.godcodecamera.app.camera.util.EffectUtil;
import com.uboss.godcodecamera.app.camera.util.GPUImageFilterTools;
import com.uboss.godcodecamera.app.model.Addon;
import com.uboss.godcodecamera.app.model.TagItem;
import com.uboss.godcodecamera.app.ui.MakeQrcodeActivity;
import com.uboss.godcodecamera.app.ui.MyGodCodeActivity;
import com.uboss.godcodecamera.app.volley.VolleyErrorUtil;
import com.uboss.godcodecamera.base.GodeCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import butterknife.ButterKnife;
import butterknife.InjectView;
import it.sephiroth.android.library.widget.HListView;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * 图片处理界面
 * --cyan 加贴纸
 * Created by sky on 2015/7/8.
 * Weibo: http://weibo.com/2030683111
 * Email: 1132234509@qq.com
 */
public class PhotoProcessActivity extends CameraBaseActivity {

    private static final String TAG = "PhotoProcessActivity";
    //贴纸、滤镜图片 --cyan图片处理区域
    @InjectView(R.id.gpuimage)
    GPUImageView mGPUImageView;
    //绘图区域
    @InjectView(R.id.drawing_view_container)
    ViewGroup drawArea;
    //底部按钮
//    @InjectView(R.id.sticker_btn)
//    TextView stickerBtn;    //贴纸按钮
//    @InjectView(R.id.filter_btn)
//    TextView filterBtn;    //滤镜按钮
//    @InjectView(R.id.text_btn)
//    TextView labelBtn;      //标签按钮
    //工具区
    @InjectView(R.id.list_tools)
    HListView bottomToolBar;    //--cyan 包括贴纸，滤镜，标签选择 HListView 是一个横向的listview类，包括在gradle中
    @InjectView(R.id.toolbar_area)
    ViewGroup toolArea;
    @InjectView(R.id.img_title_right)
    Button img_title_right;
    @InjectView(R.id.img_title_left)
    ImageView img_title_left;
//    @InjectView(R.id.rl_bottom_toolbar)
//    RelativeLayout rl_bottom_toolbar;


    private MyImageViewDrawableOverlay mImageView;
    private LabelSelector labelSelector;    //贴纸 --cyan

    //当前选择底部按钮
    private TextView currentBtn;
    //当前图片
    private Bitmap currentBitmap;
    //用于预览的小图片
    private Bitmap smallImageBackgroud;
    //小白点标签 --cyan 文字标签
    private LabelView emptyLabelView;

    private List<LabelView> labels = new ArrayList<LabelView>();

    //标签区域
    private View commonLabelArea;

    //--cyan
    SimpleAdapter model_adapter;
    ArrayList<String> arr_model_title = new ArrayList<String>();
    static ArrayList<Integer> addonList = new ArrayList<Integer>();
    static ArrayList<String> arr_model_instruction = new ArrayList<String>();
    ArrayList<HashMap<String,Object>> models = new ArrayList<>();
    private String content = ""; //二维码内容文字
    private int count = 0;  //二维码内容图片数量
    private String article_url = "";    //二维码内容地址
    //生成图文post 参数
    private String code,platform,black_code,article_content,poi_uid,poi_city,poi_name;
    private int template_id;


    static{
        Log.i(TAG,"addonList static");
        addonList.add(R.mipmap.sticker1_1);
        addonList.add(R.mipmap.sticker2_1);
        addonList.add(R.mipmap.sticker3_1);
        addonList.add(R.mipmap.sticker4_1);
        addonList.add(R.mipmap.sticker5_1);
        addonList.add(R.mipmap.sticker6_1);
        addonList.add(R.mipmap.sticker7_1);
        addonList.add(R.mipmap.sticker8_1);
        addonList.add(R.mipmap.sticker9_1);

    }
    static {
        arr_model_instruction.add("美好一天");
        arr_model_instruction.add("美味情缘");
        arr_model_instruction.add("让你微笑");
        arr_model_instruction.add("这是神码");
        arr_model_instruction.add("单身无罪");
        arr_model_instruction.add("狂欢认证");
        arr_model_instruction.add("性感红唇");
        arr_model_instruction.add("夜露死苦");
        arr_model_instruction.add("最难将息");


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        ButterKnife.inject(this);
        EffectUtil.clear();
        setModelData();
        initView();
        initEvent();
        initStickerToolBar();

        //获取二维码内容
        getQrCodeContent();
        //显示之前选中的图片 --cyan
        //getData()为uri格式
//        String imagePath = LocalDataUtil.ReadSharePre("main_pictures","mainPic");
        String imagePath = CameraActivity.Main_Photo_Name;
        Log.i(TAG,"imagePath=="+imagePath);
        StringBuilder sb = new StringBuilder("file://");
        sb.append(imagePath);
//        Uri uri = Uri.fromFile(new File(imagePath));
//        Log.i(TAG," LocalDataUtil.pathToUri::"+LocalDataUtil.pathToUri(imagePath).getPath());


        ImageUtils.asyncLoadImage(this, Uri.parse(sb.toString()), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                currentBitmap = result;
                mGPUImageView.setImage(currentBitmap);
                addSticker(0);
            }
        });
//        ImageUtils.asyncLoadImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
//            @Override
//            public void callback(Bitmap result) {
//                currentBitmap = result;
//                mGPUImageView.setImage(currentBitmap);
//            }
//        });

        //用于滤镜预览的小图 --cyan
        ImageUtils.asyncLoadSmallImage(this, getIntent().getData(), new ImageUtils.LoadImageCallback() {
            @Override
            public void callback(Bitmap result) {
                smallImageBackgroud = result;
            }
        });

    }

    private void getQrCodeContent() {
        Intent intent = getIntent();
        code = intent.getStringExtra("code");
        platform = intent.getStringExtra("platform");
        black_code = intent.getStringExtra("black_code");
        article_content = intent.getStringExtra("article_content");
        poi_uid = intent.getStringExtra("poi_uid");
        poi_city = intent.getStringExtra("poi_city");
        poi_name = intent.getStringExtra("poi_name");
        template_id = intent.getIntExtra("use_model",1);
    }

    private void initView() {
        //添加贴纸水印的画布,绘图区域 --cyan
        View overlay = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_drawable_overlay, null);
        mImageView = (MyImageViewDrawableOverlay) overlay.findViewById(R.id.drawable_overlay);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(App.getApp().getScreenWidth(),
                App.getApp().getScreenWidth());
        mImageView.setLayoutParams(params);
        overlay.setLayoutParams(params);
        drawArea.addView(overlay);


        //添加标签选择器 --cyan 标签就是 "心情，地点"
        RelativeLayout.LayoutParams rparams = new RelativeLayout.LayoutParams(App.getApp().getScreenWidth(), App.getApp().getScreenWidth());
        labelSelector = new LabelSelector(this);
        labelSelector.setLayoutParams(rparams);
        drawArea.addView(labelSelector);
        labelSelector.hide();   //"心情，地点" 隐藏 --cyan

        //初始化滤镜图片
        mGPUImageView.setLayoutParams(rparams);


        //初始化空白标签
        emptyLabelView = new LabelView(this);
        emptyLabelView.setEmpty();
        EffectUtil.addLabelEditable(mImageView, drawArea, emptyLabelView,
                mImageView.getWidth() / 2, mImageView.getWidth() / 2);
        emptyLabelView.setVisibility(View.INVISIBLE); //文字标签 隐藏 --cyan

        //初始化推荐标签栏 --cyan 推荐的文字标签
        commonLabelArea = LayoutInflater.from(PhotoProcessActivity.this).inflate(
                R.layout.view_label_bottom,null);
        commonLabelArea.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        toolArea.addView(commonLabelArea);
        commonLabelArea.setVisibility(View.GONE);

        //--cyan
        img_title_left.setImageResource(R.mipmap.back);
        img_title_right.setText("完成");

    }

    /**
     * 底部 贴纸，滤镜，标签点击事件 --cyan
     */
    private void initEvent() {
//        stickerBtn.setOnClickListener(v ->{
//            if (!setCurrentBtn(stickerBtn)) {
//                return;
//            }
//            bottomToolBar.setVisibility(View.VISIBLE);
//            labelSelector.hide();
//            emptyLabelView.setVisibility(View.GONE);
//            commonLabelArea.setVisibility(View.GONE);
//            initStickerToolBar();
//        });

//        filterBtn.setOnClickListener(v -> {
//            if (!setCurrentBtn(filterBtn)) {
//                return;
//            }
//            bottomToolBar.setVisibility(View.VISIBLE);
//            labelSelector.hide();
//            emptyLabelView.setVisibility(View.INVISIBLE);
//            commonLabelArea.setVisibility(View.GONE);
//            initFilterToolBar();
//        });
//        labelBtn.setOnClickListener(v -> {
//            if (!setCurrentBtn(labelBtn)) {
//                return;
//            }
//            bottomToolBar.setVisibility(View.GONE);
//            labelSelector.showToTop();
//            commonLabelArea.setVisibility(View.VISIBLE);
//
//        });
        //点击图片添加标签
//        labelSelector.setTxtClicked(v -> {
//            EditTextActivity.openTextEdit(PhotoProcessActivity.this,"",8, AppConstants.ACTION_EDIT_LABEL);
//        });
//        labelSelector.setAddrClicked(v -> {
//            EditTextActivity.openTextEdit(PhotoProcessActivity.this,"",8, AppConstants.ACTION_EDIT_LABEL_POI);
//
//        });
        mImageView.setOnDrawableEventListener(wpEditListener);
//        mImageView.setSingleTapListener(()->{
//            //显示添加标签
//                emptyLabelView.updateLocation((int) mImageView.getmLastMotionScrollX(),
//                        (int) mImageView.getmLastMotionScrollY());
//                emptyLabelView.setVisibility(View.VISIBLE);
//
//                labelSelector.showToTop();
//                drawArea.postInvalidate();
//        });
//        labelSelector.setOnClickListener(v -> {
//            labelSelector.hide();
//            emptyLabelView.updateLocation((int) labelSelector.getmLastTouchX(),
//                    (int) labelSelector.getmLastTouchY());
//            emptyLabelView.setVisibility(View.VISIBLE);
//        });


//        titleBar.setRightBtnOnclickListener(v -> {
//            savePicture();
//        });
        img_title_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog("正在保存图片");
                CreateNewArticle();
//                String file_path = FileUtils.getQrcodePath();
//                String qrcode_url = AppConstants.HOME_URL+article_url;
//                Log.i(TAG,"qrcode_url"+qrcode_url);
//                if(QRCodeUtil.createQRImage(qrcode_url,200,200,null, file_path)){
//                    Log.i(TAG,"create qrcode success");
//                    EffectUtil.hightlistViews.get(0).updateContent(PhotoProcessActivity.this, BitmapFactory.decodeFile(file_path));
//                }else {
//                    Log.e(TAG,"create qrcode failed");
//                }
//                savePicture();

            }
        });
    }

    private void CreateNewArticle() {
        String url = AppConstants.HOME_URL+"/articles/new";
        JSONObject params = new JSONObject();
        try {
            params.put("code",code);
            params.put("platform",platform);
            params.put("black_code",black_code);
            params.put("article_content",article_content);
            params.put("template_id",template_id);
            params.put("poi_uid",poi_uid);
            params.put("poi_city",poi_city);
            params.put("poi_name",poi_name);
            Log.e(TAG,"CreateNewArticle params::"+params.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest request_new_article = new JsonObjectRequest(Request.Method.POST, url,params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Log.i(TAG,"new article Success!");
                try {
                    article_url = jsonObject.getString("url");
                    //格式如下 article_url==/articles/97
                    Log.e(TAG,"article_url=="+article_url);

                    String file_path = FileUtils.getQrcodePath();
                    String qrcode_url = AppConstants.HOME_URL+article_url;
                    Log.i(TAG,"qrcode_url"+qrcode_url);
                    if(QRCodeUtil.createQRImage(qrcode_url,200,200,null, file_path)){
                        Log.i(TAG,"create qrcode success");
                        EffectUtil.hightlistViews.get(0).updateContent(PhotoProcessActivity.this, BitmapFactory.decodeFile(file_path));
                        savePicture();

                    }else {
                        Log.e(TAG,"create qrcode failed");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyErrorUtil.onErrorResponse(volleyError);

            }
        });
        App.startVolley(request_new_article);
    }

    //保存图片
    private void savePicture(){
        //加滤镜
        final Bitmap newBitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newBitmap);
        RectF dst = new RectF(0, 0, mImageView.getWidth(), mImageView.getHeight());
        try {
            cv.drawBitmap(mGPUImageView.capture(), null, dst, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
            cv.drawBitmap(currentBitmap, null, dst, null);
        }
        //加贴纸水印
        EffectUtil.applyOnSave(cv, mImageView);

        new SavePicToFileTask().execute(newBitmap);
    }

    private class SavePicToFileTask extends AsyncTask<Bitmap,Void,String>{
        Bitmap bitmap;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressDialog("图片处理中...");
        }

        @Override
        protected String doInBackground(Bitmap... params) {
            String fileName = null;
            try {
                bitmap = params[0];
                /**
                 * --cyan* 添加图片信息，并保存图片
                 * ***********标签未保存
                 */
                String picName = TimeUtils.dtFormat(new Date(), "yyyyMMddHHmmss");
                 fileName = ImageUtils.saveToFile(FileUtils.getInst().getPhotoSavedPath() + "/"+ picName+".jpg", false, bitmap);

            } catch (Exception e) {
                e.printStackTrace();
                toast("图片处理错误，请退出相机并重试", Toast.LENGTH_LONG);
            }
            return fileName;
        }

        @Override
        protected void onPostExecute(String fileName) {
            super.onPostExecute(fileName);
            dismissProgressDialog();
            if (StringUtils.isEmpty(fileName)) {
                return;
            }
            saveQrData(fileName);

            //将照片信息保存至sharedPreference
            //保存标签信息
            //--cyan* ************去掉标签内容
//            List<TagItem> tagInfoList = new ArrayList<TagItem>();
//            for (LabelView label : labels) {
//                tagInfoList.add(label.getTagInfo());
//            }

            //将图片信息通过EventBus发送到MainActivity
//            FeedItem feedItem = new FeedItem(tagInfoList,fileName);
//            EventBus.getDefault().post(feedItem);
            Log.i(TAG,"filename=="+fileName);
            dismissProgressDialog();
            CameraManager.getInst().close();

            startActivity(new Intent(PhotoProcessActivity.this, MyGodCodeActivity.class));
            finish();
        }
    }
    private void saveQrData(String filename){
//        LocalDataUtil.ReadSharePre(AppConstants.SP_File_MyGodCode,)
//        LocalDataUtil.SaveSharedPre(fileName,"",AppConstants.SP_File_MyGodCode);
//        SQLiteDatabase db = openOrCreateDatabase(AppConstants.DB_MyGodCode, Context.MODE_PRIVATE, null);
//        DatabaseHelper helper = new DatabaseHelper(this);
//        SQLiteDatabase db = helper.getWritableDatabase();
        Log.i(TAG,"saveQrData()");
        String text = "";
        try {
            JSONObject json = new JSONObject(article_content);
            text = json.getString("text");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GodeCode godcode = new GodeCode(filename,article_url,text, Bimp.tempSelectBitmap.size(),filename);
        DBManager manager = new DBManager(this);
        manager.add(godcode);

//        //查询”我的神码“数据库
//        List<GodeCode>list_godcode = manager.query();
//        for(GodeCode item :list_godcode){
//            StringBuilder sb = new StringBuilder();
//            sb.append("filename==").append(item.getFilename())
//                    .append("   date==").append(item.getDate())
//                    .append("   content==").append(item.getContent()).append("  count==")
//                    .append(item.getCount()).append("   url==").append(item.getUrl());
//            Log.i(TAG,"mygodcode::"+sb.toString());
//
//        }

    }


    public void tagClick(View v){
        TextView textView = (TextView)v;
        TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,textView.getText().toString());
        addLabel(tagItem);
    }

    private MyImageViewDrawableOverlay.OnDrawableEventListener wpEditListener   = new MyImageViewDrawableOverlay.OnDrawableEventListener() {
        @Override
        public void onMove(MyHighlightView view) {
        }

        @Override
        public void onFocusChange(MyHighlightView newFocus, MyHighlightView oldFocus) {
        }

        @Override
        public void onDown(MyHighlightView view) {

        }

        @Override
        public void onClick(MyHighlightView view) {
            labelSelector.hide();
        }

        @Override
        public void onClick(final LabelView label) {
            if (label.equals(emptyLabelView)) {
                return;
            }
            alert("温馨提示", "是否需要删除该标签！", "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EffectUtil.removeLabelEditable(mImageView, drawArea, label);
                    labels.remove(label);
                }
            }, "取消", null);
        }
    };

    /**
     * 绘制当前按钮标识 小圆点 --cyan
     * @param btn
     * @return
     */
    private boolean setCurrentBtn(TextView btn) {
        if (currentBtn == null) {
            currentBtn = btn;
        } else if (currentBtn.equals(btn)) {
            return false;
        } else {
            //可以在上、下、左、右设置图标 --cyan
            // setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom)
            currentBtn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            // 第二种方法：setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom)
            //但是Drawable必须已经setBounds(Rect)
        }
        Drawable myImage = getResources().getDrawable(R.drawable.select_icon);
        btn.setCompoundDrawablesWithIntrinsicBounds(null, null, null, myImage);
        currentBtn = btn;
        return true;
    }


    //初始化贴纸 --cyan
    private void initStickerToolBar(){
        Log.w(TAG,"initStickerToolBar()");
//        bottomToolBar.setAdapter(new StickerToolAdapter(PhotoProcessActivity.this, EffectUtil.addonList));//EffectUtil.addonList 默认贴纸列表
//        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
//                                    View arg1, int arg2, long arg3) {
//                labelSelector.hide();       //之前选择的贴纸隐藏
//                Addon sticker = EffectUtil.addonList.get(arg2);
//                EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
//                        new EffectUtil.StickerCallback() {
//                            @Override
//                            public void onRemoveSticker(Addon sticker) {
//                                labelSelector.hide();
//                            }
//                        });
//            }
//        });
//        setCurrentBtn(stickerBtn);


        //--cyan start
        model_adapter= new SimpleAdapter(PhotoProcessActivity.this,getToolList(),R.layout.item_sticker,
                new String[]{"title","img","instruction"},
                new int[]{R.id.tv_model_title,R.id.img_model,R.id.tv_model_instruction});
        bottomToolBar.setAdapter(model_adapter);
        View bottom_view = LayoutInflater.from(PhotoProcessActivity.this).inflate(R.layout.item_bottom_toolbar,null);
        bottom_view.findViewById(R.id.rl_bottom_toolbar).setBackgroundColor(getResources().getColor(R.color.black));

        bottomToolBar.setOnItemClickListener( new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0,
                                    View arg1, int position, long arg3) {
                addSticker(position+1);

//                Toast.makeText(PhotoProcessActivity.this,position+"",Toast.LENGTH_SHORT).show();
//                labelSelector.hide();       //之前选择的贴纸隐藏
//                Addon sticker = EffectUtil.addonList.get(position+1);//添加贴纸200*200 --cyan*** 0是二维码
//
//                EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
//                        new EffectUtil.StickerCallback() {
//                            @Override
//                            public void onRemoveSticker(Addon sticker) {
//                                labelSelector.hide();
//                            }
//                        });
//                switch (position){
//                    case 0:
//
//                        model_adapter.notifyDataSetChanged();
//                        break;
//                    case 1:
//
//                        model_adapter.notifyDataSetChanged();
//                        break;
//
//                    default:
//
//                        break;
//
//                }


            }
        });

        //--cyan end
    }

    /**
     * 添加贴纸在图片上
     * @param position
     */
    private void addSticker(int position){
        labelSelector.hide();       //之前选择的贴纸隐藏
        Addon sticker = EffectUtil.addonList.get(position);//添加贴纸200*200 --cyan*** 0是二维码

        EffectUtil.addStickerImage(mImageView, PhotoProcessActivity.this, sticker,
                new EffectUtil.StickerCallback() {
                    @Override
                    public void onRemoveSticker(Addon sticker) {
                        labelSelector.hide();
                    }
                });
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
        while (arr_model_title.size()<addonList.size()){
            arr_model_title.add("");
        }
//        Log.e(TAG,"title=="+arr_model_title.size()+" addonList=="+addonList.size()+" instru=="+arr_model_instruction.size());

    }
    //初始化滤镜 --cyan
    private void initFilterToolBar(){
        final List<FilterEffect> filters = EffectService.getInst().getLocalFilters();//--cyan* 初始化滤镜
        final FilterAdapter adapter = new FilterAdapter(PhotoProcessActivity.this, filters,smallImageBackgroud);
        bottomToolBar.setAdapter(adapter);
        bottomToolBar.setOnItemClickListener(new it.sephiroth.android.library.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(it.sephiroth.android.library.widget.AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                labelSelector.hide();//原来滤镜隐藏
                if (adapter.getSelectFilter() != arg2) {
                    adapter.setSelectFilter(arg2);
                    GPUImageFilter filter = GPUImageFilterTools.createFilterForType(
                            PhotoProcessActivity.this, filters.get(arg2).getType());
                    mGPUImageView.setFilter(filter);
                    GPUImageFilterTools.FilterAdjuster mFilterAdjuster = new GPUImageFilterTools.FilterAdjuster(filter);
                    //可调节颜色的滤镜
                    if (mFilterAdjuster.canAdjust()) {
                        //mFilterAdjuster.adjust(100); 给可调节的滤镜选一个合适的值
                    }
                }
            }
        });
    }

    //添加标签 --cyan
    private void addLabel(TagItem tagItem) {
        labelSelector.hide();
        emptyLabelView.setVisibility(View.INVISIBLE);
        if (labels.size() >= 5) {       //--cyan* 最多标签个数
            alert("温馨提示", "您只能添加5个标签！", "确定", null, null, null, true);
        } else {
            int left = emptyLabelView.getLeft();
            int top = emptyLabelView.getTop();
            if (labels.size() == 0 && left == 0 && top == 0) {
                left = mImageView.getWidth() / 2 - 10;
                top = mImageView.getWidth() / 2;
            }
            LabelView label = new LabelView(PhotoProcessActivity.this);
            label.init(tagItem);
            EffectUtil.addLabelEditable(mImageView, drawArea, label, left, top);   //添加可编辑移动的标签
            labels.add(label);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG,"onActivityResult");
        labelSelector.hide();
        super.onActivityResult(requestCode, resultCode, data);
        if (AppConstants.ACTION_EDIT_LABEL== requestCode && data != null) {
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(StringUtils.isNotEmpty(text)){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_TAG,text);
                addLabel(tagItem);
            }
        }else if(AppConstants.ACTION_EDIT_LABEL_POI== requestCode && data != null){
            String text = data.getStringExtra(AppConstants.PARAM_EDIT_TEXT);
            if(StringUtils.isNotEmpty(text)){
                TagItem tagItem = new TagItem(AppConstants.POST_TYPE_POI,text);
                addLabel(tagItem);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Bimp.tempSelectBitmap = null;
        MakeQrcodeActivity.city = null;
        MakeQrcodeActivity.shop_name = null;
    }
}
