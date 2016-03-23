package com.uboss.godcodecamera.app.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.PublicWay;
import com.baidu.location.BDLocation;

//import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ShopLocationActivity extends AppCompatActivity{

    //	private QueryDB queryDB = new QueryDB();
//	DbOfMyRoute dbOfMyRoute;
    private AutoCompleteTextView autoTxt;
    protected static final String TAG = "ShopLocationActivity";
    ArrayList<String> arrayList_autoStrs = new ArrayList<String>();
    String location = "美食";
    String uid = "";
    private SuggestionSearch mSuggestionSearch = null;
    private SimpleAdapter adapter_recLocation;
//    private ArrayAdapter<String> adapter_hisLocation;

    private List<Map<String, Object>> list_aoutoCom;
    private ListView lv_recLocation;
//    private ListView lv_historyLocation;
//    private TextView tv_noHistoryLocation;
//    private List<String> history_location_list;
    private Button btn_search;
    private PoiSearch mPoiSearch = null;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListenner();
    public String city = "";
    public double location_latitude;
    public double location_longitude;
//    private ArrayList<String> poilist = new ArrayList<String>();
    private ArrayList<HashMap<String,Object>> poiInfo = new ArrayList<HashMap<String,Object>>();
    private ImageView img_title_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        PublicWay.activityList.add(this);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_shop_location);

        init();


    }
    /*
     * 推荐地点
    */
//    OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {
//
//        @Override
//        public void onGetSuggestionResult(SuggestionResult res) {
//            // TODO Auto-generated method stub
//            if (res == null || res.getAllSuggestions() == null) {
//                return;
//            }
//            list_aoutoCom.clear();
//            poiInfo.clear();
//            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
//                Log.i(TAG,"key=="+info.key+" uid=="+info.uid+" describe=="+info.describeContents());
//                if (info.key != null){
//
////                    setListData(info.uid,info.key);
//                }
//            }
//            Log.e(TAG,"suggest::"+poiInfo.toString());
//            adapter_recLocation.notifyDataSetChanged();
//        }
//    };

    /*
     *
     */
    public void init(){
        Log.e(TAG,"init()");
//        btn_search = (Button)findViewById(R.id.btn_search);
//        btn_search.setOnClickListener(clickListener);
        Button img_title_right = (Button)findViewById(R.id.img_title_right);
        img_title_right.setVisibility(View.GONE);
        ImageView btn_title_left = (ImageView)findViewById(R.id.img_title_left);
        btn_title_left.setImageResource(R.mipmap.back);
        btn_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                onBackPressed();
                startActivity(new Intent(ShopLocationActivity.this,MakeQrcodeActivity.class));
                finish();
            }
        });
        autoTxt = (AutoCompleteTextView)findViewById(R.id.input_location);
        list_aoutoCom = new ArrayList<Map<String,Object>>();
        adapter_recLocation = new SimpleAdapter(this, list_aoutoCom, R.layout.item_set_location,
                new String[]{"prompt","address"}, new int[]{R.id.tv_prompt,R.id.tv_poi_address});
        lv_recLocation = (ListView) findViewById(R.id.lv_prompt);
        lv_recLocation.setAdapter(adapter_recLocation);
        autoTxt.addTextChangedListener(watcher);
        lv_recLocation.setOnItemClickListener(itemlistener);

        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);

//        mSuggestionSearch = SuggestionSearch.newInstance();
//        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
//        Log.e(TAG,"before()");
        initLocation();
        mLocationClient.start();
//        Log.e(TAG,"after()");

    }
    private void initLocation(){
//        Log.e(TAG,"initLocation()");
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
//        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
//        int span=1000;
        option.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的

        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
//        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到 --cyan**
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
//        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
//        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    /**
     * Poi检索监听
     *
     */
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            Log.e(TAG,"onGetPoiResult");
            //获取POI检索结果
            if (result == null
                    || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(ShopLocationActivity.this, "未找到结果", Toast.LENGTH_LONG)
                        .show();
                Log.e(TAG,"未找到结果");

                return;
            }
            if (result.error == SearchResult.ERRORNO.NO_ERROR) {
                poiInfo.clear();
                list_aoutoCom.clear();
                for(PoiInfo info:result.getAllPoi()){
                    Log.i(TAG,"name=="+info.name+" id"+info.uid);
                    setListData(info.uid,info.name,info.address);
                }
                adapter_recLocation.notifyDataSetChanged();
                return;
            }
            if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
                Log.e(TAG,"AMBIGUOUS_KEYWORD");

                // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
                String strInfo = "在";
                for (CityInfo cityInfo : result.getSuggestCityList()) {
                    strInfo += cityInfo.city;
                    strInfo += ",";
                }
                strInfo += "找到结果";
                Toast.makeText(ShopLocationActivity.this, strInfo, Toast.LENGTH_LONG)
                        .show();
            }

        }

        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }


    };

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub

        }


        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub
//			lv_historyLocation.setVisibility(8);

            location = arg0.toString();
            Log.i(TAG, "afterTextChanged "+location);
            startSearchNearby();
//            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
//                    .city(city)
//                    .keyword(location))
//                    ;

        }
    };

    void startSearchNearby(){
        Log.i(TAG,"input=="+location+" location_latitude=="+location_latitude+" location_longitude=="+location_longitude);
        PoiNearbySearchOption option = new PoiNearbySearchOption();
        option.keyword(location).location(new LatLng(location_latitude,location_longitude))
                .radius(5000);
        mPoiSearch.searchNearby(option);
    }

    private void setListData(String id,String name,String address){
        HashMap<String,Object> map = new HashMap<String,Object>();
        map.put("id",id);
        map.put("name",name);
        map.put("address",address);
        poiInfo.add(map);

        HashMap<String,Object> map_autoCom = new HashMap<String,Object>();
        map_autoCom.put("prompt",name);
        map_autoCom.put("address",address);
        list_aoutoCom.add(map_autoCom);
    }
    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            Log.i(TAG,"onReceiveLocation()");
            if (location == null ) {
                return;
            }
            mLocationClient.stop();
            Log.e(TAG,"Location ID=="+location.getLocType());
            city = location.getCity();
            location_latitude = location.getLatitude();
            location_longitude = location.getLongitude();
            startSearchNearby();
//            list_aoutoCom.clear();
//            for(Poi poi:location.getPoiList()){
//                setListData(poi.getId(),poi.getName(),poi.describeContents());
//
//            }
//            Log.i(TAG,"city=="+city+" latitude=="+location_latitude+" longitude=="+location_longitude);
//            Log.i(TAG,"location poiInfo::"+poiInfo.toString());
//            Log.i(TAG,"list_aoutoCom::"+list_aoutoCom.toString());
//            adapter_recLocation.notifyDataSetChanged();

//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();


        }



    }
    /*
 */
    public OnItemClickListener itemlistener =  new OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            // TODO Auto-generated method stub
            hideInputMethod(arg1);
            location = list_aoutoCom.get(position).get("prompt").toString();
            uid = poiInfo.get(position).get("id").toString();
            Log.e(TAG,"location=="+location+" uid=="+uid+" city=="+city);
            Intent intent = new Intent();
            intent.putExtra("shop_name", location);
            intent.putExtra("uid",uid);
            intent.putExtra("city",city);
            setResult(RESULT_OK,intent);
            finish();
        }

    };

//    public View.OnClickListener clickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View arg0) {
//            // TODO Auto-generated method stub
//            switch(arg0.getId()){
//                case R.id.btn_search:
//                    String input=autoTxt.getText().toString();
//
////                    mPoiSearch.searchInCity(new PoiCitySearchOption().city(city).keyword(input));
//
////                    Intent intent = new Intent();
////                    intent.putExtra("type", "search_location");
////                    intent.putExtra("search_location", location);
////                    setResult(RESULT_OK,intent);
////                    finish();
//                    break;
//                default:
//                    break;
//            }
//        }
//    };



    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        //hideInputMethod();
        super.onDestroy();
//        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
        mLocationClient.stop();
    }

    private void hideInputMethod(View view) {
        InputMethodManager manager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(manager.isActive()){
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}


