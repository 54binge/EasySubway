package com.binge.easysubway;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.binge.easysubway.widget.Fab;
import com.binge.easysubway.widget.LoadingWebView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Binge on 2017/9/7.
 */

public class MainActivity extends AppCompatActivity implements ValueCallback, SimpleAdapter.OnItemClickListener<Line>, AMapLocationListener {

    private static final String TAG = "MainActivity";

    private SimpleAdapter<Line> mSimpleAdapter;
    private MaterialSheetFab<? extends View> mMaterialSheetFab;
    private MyJavaScript mJavaScript;
    private List<Line> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadingWebView loadingWebView = (LoadingWebView) findViewById(R.id.mWebView);
        loadingWebView.loadUrl("file:///android_asset/subway.html");

        mJavaScript = new MyJavaScript(new Handler());
        mJavaScript.setWebView(loadingWebView.getWebView());
        loadingWebView.addJavascriptInterface(mJavaScript, "MyJavaScript");

        mJavaScript.setValueCallback(this);

        int fabColor = ContextCompat.getColor(this, R.color.fab_color);
        int dialogColor = ContextCompat.getColor(this, R.color.dialog_bg_color);

        Fab showLineBtn = (Fab) findViewById(R.id.mShowLineBtn);
        View fabSheet = findViewById(R.id.mFabSheet);
        mMaterialSheetFab = new MaterialSheetFab<>(showLineBtn, fabSheet, findViewById(R.id.mOverlay), dialogColor, fabColor);
        mMaterialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                mJavaScript.getLines();
            }
        });

        mSimpleAdapter = new SimpleAdapter<Line>(R.layout.layout_item_line, mDataList) {

            @Override
            public void onBindViewData(RecyclerView.ViewHolder holder, Line line) {
                holder.itemView.findViewById(R.id.mLineIcon).setBackgroundColor(Color.parseColor("#" + line.color));
                ((TextView) holder.itemView.findViewById(R.id.mLineName)).setText(line.name);
            }
        };

        mSimpleAdapter.setOnItemClickListener(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mSimpleAdapter);

        initLocationClient();
    }

    //声明mlocationClient对象
    public AMapLocationClient mLocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;

    private void initLocationClient() {
        mLocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除

    }

    @Override
    protected void onResume() {
        super.onResume();
        //启动定位
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void onBackPressed() {
        if (mMaterialSheetFab.isSheetVisible()) {
            mMaterialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onReceiveValue(String requestCode, String value) {
        Log.d(TAG, "onReceiveValue: 收到的data: " + value);
        Type type = new TypeToken<List<Line>>() {
        }.getType();
        List<Line> lineList = new Gson().fromJson(value, type);
        mSimpleAdapter.updateData(lineList);
    }

    @Override
    public void onItemClick(Line line) {
        mMaterialSheetFab.hideSheet();
        if (mJavaScript != null) {
            mJavaScript.selectLine(line.id);
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mJavaScript != null) {
            mJavaScript.locationNow(String.valueOf(aMapLocation.getLongitude()), String.valueOf(aMapLocation.getLatitude()));
        }
    }
}
