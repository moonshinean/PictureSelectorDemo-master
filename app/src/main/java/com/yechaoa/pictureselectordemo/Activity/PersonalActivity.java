package com.yechaoa.pictureselectordemo.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.zackratos.ultimatebar.UltimateBar;
import com.yechaoa.pictureselectordemo.Modle.RSpostData;
import com.yechaoa.pictureselectordemo.Modle.ResultData;
import com.yechaoa.pictureselectordemo.Modle.ReturnPostData;
import com.yechaoa.pictureselectordemo.Modle.SidSelectData;
import com.yechaoa.pictureselectordemo.Util.DataDBHepler;
import com.yechaoa.pictureselectordemo.R;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by moonshine on 2018/3/17.
 */

public class PersonalActivity extends Activity {


    private TextView nameTv;
    private TextView phoneTv;
    private TextView idnumTv;
    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);
        init();
        /**
         * 设置状态栏的颜色
         */
        UltimateBar.newColorBuilder()
                .statusColor(Color.parseColor("#000000"))       // 状态栏颜色
                .statusDepth(30)                // 状态栏颜色深度
                .build(this)
                .apply();
        ImageView imageView = (ImageView) findViewById(R.id.return_view);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new AnotherTask().execute("");
            }
        }).start();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("index", "1");
                intent.setClass(PersonalActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

        /**
         *获取Sid数据
         */

    public String selectSid(){
        DataDBHepler dataDBHepler = new DataDBHepler(getBaseContext());
        ArrayList<SidSelectData> DataList = dataDBHepler.FindSidData();
        final SidSelectData data = new SidSelectData(DataList.get(0).getId(),DataList.get(0).getSid());
        Log.i(TAG,"数据库的sid为："+data.getSid());
        final String Msid = data.getSid();
        return Msid;
    }

        private class AnotherTask extends AsyncTask<String, Void, String> {
            @Override
            protected void onPostExecute(String result) {
                //对UI组件的更新操作
                Gson gson = new Gson();
                try {
                    RSpostData rSpostData= gson.fromJson(result, RSpostData.class);
                    Log.i(TAG,"data数据为："+rSpostData.getData());

                    ReturnPostData returnPostData = rSpostData.getData();

                    nameTv.setText(returnPostData.getRealName());
                    phoneTv.setText(returnPostData.getPhone());
                    idnumTv.setText(returnPostData.getIdCode());

                }catch (Exception e){
                    Log.e(TAG, "postlisthttp: ",e );
                }
            }
            @Override
            protected String doInBackground(String... params) {
                //耗时的操作
                String SidStatus = null;
                String result = null;
                String url = "http://119.23.219.22:80/element-admin/user/query-self";
                OkHttpClient client = new OkHttpClient();
                Gson gson = new Gson();

                ResultData mdata = new ResultData();
                String sid =selectSid();

                mdata.setSid(sid);

                String json = gson.toJson(mdata);//将其转换为JSON数据格式

                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

                RequestBody requestBody = RequestBody.create(mediaType, json);//放进requestBoday中
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    result = response.body().string();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "doInBackground: ",e );
                }
                return result;
            }
        }

    public  void init(){
        nameTv = (TextView) findViewById(R.id.personal_name);
        phoneTv = (TextView) findViewById(R.id.personal_phone);
        idnumTv = (TextView) findViewById(R.id.personal_idnum);
    }
}
