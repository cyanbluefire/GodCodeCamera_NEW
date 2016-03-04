package com.uboss.godcodecamera.app.ui;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.Database.DBManager;
import com.uboss.godcodecamera.base.GodeCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


public class MyGodCodeActivity extends AppCompatActivity {

    private static final String TAG = "MyGodCodeActivity";
    private RecyclerViewAdapter mAdapter;
    private List<GodeCode> list_godcode;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gode_code);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_my_godcode);
        mLayoutManager = new GridLayoutManager(App.getContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setData();
//        mAdapter = new RecyclerViewAdapter(MyGodCodeActivity.this,data);
        mAdapter = new RecyclerViewAdapter(MyGodCodeActivity.this,list_godcode);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setData() {
//        JSONObject json = new JSONObject();
//        json.put()
        //查询”我的神码“数据库
        DBManager manager = new DBManager(this);
        list_godcode = manager.query();
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

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        ImageView img_qrcode_photo;
        TextView tv_qrcode_content;
        TextView tv_qrcode_count;


        private static final String TAG = "RecyclerViewAdapter";
//        private ArrayList<String> mDataset;

        Context mContext;
        JSONArray jsonArray_added_sku;
//        public RecyclerViewAdapter(MyGodCodeActivity context, JSONArray jsonArray_added_sku) {
//            mContext = context;
//            this.jsonArray_added_sku = jsonArray_added_sku;
//        }

        //ViewHolder 设置循环使用的view
        public  class ViewHolder extends  RecyclerView.ViewHolder{
            public View mView;
            public ViewHolder(View v){
                super(v);
                mView = v;
            }
        }
        public RecyclerViewAdapter(Context context,List<GodeCode> list) {
            list_godcode = list;
            mContext = context;
        }

        @Override
        public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_godcode,parent,false);
            ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
            tv_qrcode_content = (TextView) holder.mView.findViewById(R.id.tv_qrcode_content);
            img_qrcode_photo = (ImageView)holder.mView.findViewById(R.id.img_qrcode_photo);
            tv_qrcode_count = (TextView)holder.mView.findViewById(R.id.tv_qrcode_count);


            GodeCode godcode = list_godcode.get(position);
            img_qrcode_photo.setImageBitmap(BitmapFactory.decodeFile(godcode.getFilename()));
            tv_qrcode_content.setText(godcode.getContent());
            StringBuffer sb = new StringBuffer();
            sb.append("共").append(godcode.getCount()).append("张");
            tv_qrcode_count.setText(sb.toString());



        }


        @Override
        public int getItemCount() {
            return list_godcode.size();
        }


    }


}
