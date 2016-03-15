package com.uboss.godcodecamera.app.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uboss.godcodecamera.App;
import com.uboss.godcodecamera.AppConstants;
import com.uboss.godcodecamera.R;
import com.uboss.godcodecamera.app.Database.DBManager;
import com.uboss.godcodecamera.app.camera.ui.CameraActivity;
import com.uboss.godcodecamera.base.GodeCode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MyGodCodeActivity extends AppCompatActivity {

    private static final String TAG = "MyGodCodeActivity";
    private RecyclerViewAdapter mAdapter;
    private  static List<GodeCode> list_godcode = new ArrayList<GodeCode>() {
    };

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @InjectView(R.id.img_title_right)
    Button img_title_right;
    @InjectView((R.id.img_title_left))
    ImageView img_title_left;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gode_code);
        ButterKnife.inject(this);

        init();
    }

    private void init() {
        img_title_right.setVisibility(View.GONE);
        img_title_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyGodCodeActivity.this, CameraActivity.class));
                finish();
            }
        });
        mRecyclerView = (RecyclerView)findViewById(R.id.rv_my_godcode);
        mLayoutManager = new GridLayoutManager(App.getContext(),1);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setData();
//        mAdapter = new RecyclerViewAdapter(MyGodCodeActivity.this,data);
        mAdapter = new RecyclerViewAdapter(MyGodCodeActivity.this,list_godcode);
        mAdapter.setOnItemClickLitener(new RecyclerViewAdapter.OnItemClickLitener()
        {

            @Override
            public void onItemClick(View view, int position)
            {
//                Toast.makeText(MyGodCodeActivity.this,"点击了"+position,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyGodCodeActivity.this,GodCodeWebActivity.class);
                GodeCode godcode = list_godcode.get(position);
                String url = AppConstants.HOME_URL+godcode.getUrl();
                Log.i(TAG,"url=="+url);
                intent.putExtra("article_url",url);
                intent.putExtra("isPreview",false);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position)
            {

            }
        });
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//        });

    }

    private void setData() {
//        JSONObject json = new JSONObject();
//        json.put()
        //查询”我的神码“数据库
        DBManager manager = new DBManager(this);
        List<GodeCode> temp = manager.query();
        //从后往前，确保最新的在前面
        for(int i=temp.size()-1;i>=0;i--){
            list_godcode.add(temp.get(i));
        }

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

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        ImageView img_qrcode_photo;
        TextView tv_qrcode_content;
        TextView tv_qrcode_count;


        private static final String TAG = "RecyclerViewAdapter";
        public interface OnItemClickLitener
        {
            void onItemClick(View view, int position);
            void onItemLongClick(View view , int position);
        }

        private OnItemClickLitener mOnItemClickLitener;

        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
        {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }


        Context mContext;

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

            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null)
            {
                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemClick(holder.itemView, pos);
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
                {
                    @Override
                    public boolean onLongClick(View v)
                    {
                        int pos = holder.getLayoutPosition();
                        mOnItemClickLitener.onItemLongClick(holder.itemView, pos);
                        return false;
                    }
                });
            }

        }


        @Override
        public int getItemCount() {
            return list_godcode.size();
        }


    }


}
