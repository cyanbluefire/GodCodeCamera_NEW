package com.uboss.godcodecamera.app.ui;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class MyGodCodeActivity extends AppCompatActivity {

    private RecyclerViewAdapter mAdapter;


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
        mAdapter = new RecyclerViewAdapter(MyGodCodeActivity.this,data);
        mRecyclerView.setAdapter(mAdapter);

    }

    private void setData() {
        JSONObject json = new JSONObject();
        json.put()
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        ImageView img_qrcode_photo;
        TextView tv_qrcode_content;
        TextView tv_qrcode_count;


        private static final String TAG = "RecyclerViewAdapter";
        private ArrayList<String> mDataset;

        Context mContext;
        JSONArray jsonArray_added_sku;
        public RecyclerViewAdapter(MyGodCodeActivity context, JSONArray jsonArray_added_sku) {
            mContext = context;
            this.jsonArray_added_sku = jsonArray_added_sku;
        }

        //ViewHolder 设置循环使用的view
        public  class ViewHolder extends  RecyclerView.ViewHolder{
            public View mView;
            public ViewHolder(View v){
                super(v);
                mView = v;
            }
        }
        public RecyclerViewAdapter(Context context,ArrayList<String> myDataset) {
            mDataset = myDataset;
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







        }


        @Override
        public int getItemCount() {
            if(jsonArray_added_sku !=null){
                return jsonArray_added_sku.length();
            }else {
                return mDataset.size();
            }
        }


    }


}
