package com.uboss.godcodecamera.app;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.MyUtil.PublicWay;
import com.baidu.location.BDLocation;

import butterknife.ButterKnife;
import butterknife.InjectView;
//import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.AdapterView.OnItemClickListener;

//public class ShopLocationActivity extends AppCompatActivity {
//
//    private PoiSearch mPoiSearch = null;
//    private SuggestionSearch mSuggestionSearch = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_shop_location);
//        PublicWay.activityList.add(this);
//        ButterKnife.inject(this);
//        init();
//    }
//
//    private void init() {
//        mPoiSearch = PoiSearch.newInstance();
//        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
//        mPoiSearch.searchNearby(new PoiNearbySearchOption().)
//    }
//    OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
//        public void onGetPoiResult(PoiResult result){
//            //获取POI检索结果
//        }
//        public void onGetPoiDetailResult(PoiDetailResult result){
//            //获取Place详情页检索结果
//        }
//    };
//}

public class ShopLocationActivity extends AppCompatActivity{

    //	private QueryDB queryDB = new QueryDB();
//	DbOfMyRoute dbOfMyRoute;
    private AutoCompleteTextView autoTxt;
    protected static final String TAG = "selectLocation";
    ArrayList<String> arrayList_autoStrs = new ArrayList<String>();
    String location = "";
    private SuggestionSearch mSuggestionSearch = null;
    private SimpleAdapter adapter_recLocation;
    private ArrayAdapter<String> adapter_hisLocation;

    private List<Map<String, Object>> list_aoutoCom;
    private ListView lv_recLocation;
    private ListView lv_historyLocation;
    private TextView tv_noHistoryLocation;
    private List<String> history_location_list;
    private Button btn_search;
    private PoiSearch mPoiSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        PublicWay.activityList.add(this);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_shop_location);

        init();
		/*
		 * 推荐地点
		 */
        OnGetSuggestionResultListener listener = new OnGetSuggestionResultListener() {

            @Override
            public void onGetSuggestionResult(SuggestionResult res) {
                // TODO Auto-generated method stub
                if (res == null || res.getAllSuggestions() == null) {
                    return;
                }
                list_aoutoCom.clear();
                for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                    if (info.key != null){
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("prompt", info.key);
                        list_aoutoCom.add(map);
                    }
                }
                adapter_recLocation.notifyDataSetChanged();
            }
        };
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(listener);

		/*
		 *
		 */
        autoTxt = (AutoCompleteTextView)findViewById(R.id.input_location);
        list_aoutoCom = new ArrayList<Map<String,Object>>();
        adapter_recLocation = new SimpleAdapter(this, list_aoutoCom, R.layout.item_set_location, new String[]{"prompt"}, new int[]{R.id.tv_prompt});
        lv_recLocation = (ListView) findViewById(R.id.lv_prompt);
        lv_recLocation.setAdapter(adapter_recLocation);
        autoTxt.addTextChangedListener(watcher);
        lv_recLocation.setOnItemClickListener(itemlistener);

    }
    /*
     *
     */
    public void init(){
        btn_search = (Button)findViewById(R.id.btn_search);

        btn_search.setOnClickListener(clickListener);
        mPoiSearch = PoiSearch.newInstance();
        mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
    }
        OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener(){
        public void onGetPoiResult(PoiResult result){
            //获取POI检索结果
            Log.i(TAG,"resut.getAllAddr::"+result.getAllAddr()+"  result.getAllPoi()::"+result.getAllPoi());
        }
        public void onGetPoiDetailResult(PoiDetailResult result){
            //获取Place详情页检索结果
        }
    };
    public View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            switch(arg0.getId()){
                case R.id.btn_search:
                    String input=autoTxt.getText().toString();
                    Log.i(TAG,"input=="+input);
                    mPoiSearch.searchNearby(new PoiNearbySearchOption().keyword(input).location(new LatLng()));
//                    Intent intent = new Intent();
//                    intent.putExtra("type", "search_location");
//                    intent.putExtra("search_location", location);
//                    setResult(RESULT_OK,intent);
//                    finish();
                    break;
                default:
                    break;
            }
        }
    };
    /*
     * ����ص��б����
     */
    public OnItemClickListener itemlistener =  new OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                long arg3) {
            // TODO Auto-generated method stub
            hideInputMethod(arg1);
            location = list_aoutoCom.get(position).get("prompt").toString();
            Intent intent = new Intent();
            intent.putExtra("shop_name", location);
            setResult(RESULT_OK,intent);
            finish();
        }

    };
    /*
     * �Զ�������
     */
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
            Log.v(TAG, "afterTextChanged "+location);
            mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .city("深圳")
                    .keyword(location));

        }
    };


    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null ) {
                return;
            }
//            MyLocationData locData = new MyLocationData.Builder()
//                    .accuracy(location.getRadius())
//                    // 此处设置开发者获取到的方向信息，顺时针0-360
//                    .direction(100).latitude(location.getLatitude())
//                    .longitude(location.getLongitude()).build();


        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }



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
        mSuggestionSearch.destroy();
        mPoiSearch.destroy();
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


