package com.binge.easysubway;

import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.binge.easysubway.permission.AfterPermissionGranted;
import com.binge.easysubway.permission.EasyPermissions;
import com.binge.easysubway.widget.Fab;
import com.binge.easysubway.widget.LoadingWebView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


import static com.binge.easysubway.ConstantValue.REQUEST_CODE_GET_CITIES;
import static com.binge.easysubway.ConstantValue.REQUEST_CODE_GET_LINES;
import static com.binge.easysubway.ConstantValue.REQUEST_CODE_LOADING_COMPLETED;

/**
 * Created by Binge on 2017/9/7.
 */
       /*

*/
public class MainActivity extends AppCompatActivity implements ValueCallback, SimpleAdapter.OnItemClickListener<Line>, AMapLocationListener, EasyPermissions.PermissionCallbacks, LoadingWebView.LoadingStateListener {

    private String[] permissionArray = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
//            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS};

    private static final String TAG = "MainActivity";
    private static final int LOCATION_TIMEOUT = 20000;

    private SimpleAdapter<Line> mSimpleAdapter;
    private MaterialSheetFab<? extends View> mMaterialSheetFab;
    private MyJavaScript mJavaScript;
    private Fab mShowLineBtn;
    private View mLocation;
    public AMapLocationClient mLocationClient;
    private LocationType mCurrentLocationType = LocationType.MANUALLY;
    private boolean isMapLoadingCompleted = false;
    private View mErrorLayout;
    private LoadingWebView mLoadingWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoadingWebView = (LoadingWebView) findViewById(R.id.mWebView);
        mLoadingWebView.loadUrl("file:///android_asset/subway.html");
        mLoadingWebView.setLoadingStateListener(this);

        mErrorLayout = findViewById(R.id.errorLayout);
        findViewById(R.id.retry_tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mLoadingWebView != null){
                    mLoadingWebView.reLoad();
                }
            }
        });

        mLocation = findViewById(R.id.mLocation);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentLocationType = LocationType.MANUALLY;
                location();
            }
        });

        mJavaScript = new MyJavaScript(new Handler());
        mJavaScript.setWebView(mLoadingWebView.getWebView());
        mLoadingWebView.addJavascriptInterface(mJavaScript, "MyJavaScript");

        mJavaScript.setValueCallback(this);

        int fabColor = ContextCompat.getColor(this, R.color.fab_color);
        int dialogColor = ContextCompat.getColor(this, R.color.dialog_bg_color);

        mShowLineBtn = (Fab) findViewById(R.id.mShowLineBtn);
        View fabSheet = findViewById(R.id.mFabSheet);
        mMaterialSheetFab = new MaterialSheetFab<>(mShowLineBtn, fabSheet, findViewById(R.id.mOverlay), dialogColor, fabColor);
        mMaterialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                mJavaScript.getLines();
            }
        });

        mSimpleAdapter = new SimpleAdapter<Line>(R.layout.layout_item_line, new ArrayList<Line>()) {

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

    private void initLocationClient() {
        mLocationClient = new AMapLocationClient(this);
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        mLocationClient.setLocationListener(this);
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationOption.setOnceLocation(true);
        locationOption.setNeedAddress(false);
        locationOption.setLocationCacheEnable(true);
        locationOption.setHttpTimeOut(LOCATION_TIMEOUT);
        mLocationClient.setLocationOption(locationOption);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        if (isMapLoadingCompleted && checkPermission()) {
            mCurrentLocationType = LocationType.AUTO;
            location();
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
        switch (requestCode) {
            case REQUEST_CODE_LOADING_COMPLETED:
                Log.d(TAG, "onReceiveValue: REQUEST_CODE_LOADING_COMPLETED");
                isMapLoadingCompleted = true;
                mLocation.setVisibility(View.VISIBLE);
                mShowLineBtn.setVisibility(View.VISIBLE);
                if (checkPermission()) {
                    mCurrentLocationType = LocationType.MANUALLY;
                    location();
                }
                break;
            case REQUEST_CODE_GET_CITIES:

                break;
            case REQUEST_CODE_GET_LINES:
                Type type = new TypeToken<List<Line>>() {
                }.getType();
                List<Line> lineList = new Gson().fromJson(value, type);
                mSimpleAdapter.updateData(lineList);
                break;
        }
    }

    @Override
    public void onItemClick(Line line) {
        mMaterialSheetFab.hideSheet();
        if (mJavaScript != null) {
            mJavaScript.selectLine(line.id);
        }
    }


    @AfterPermissionGranted(100)
    public void location() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        } else {
            Log.d(TAG, "onClick: mLocationClient = null");
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        String longitude = String.valueOf(aMapLocation.getLongitude());
        String latitude = String.valueOf(aMapLocation.getLatitude());
        Log.d(TAG, "onLocationChanged: " + longitude + "<---->" + latitude);
        if (mJavaScript != null) {
            if (mCurrentLocationType == LocationType.AUTO) {
                mJavaScript.locationOnResume(longitude, latitude);
            } else if (mCurrentLocationType == LocationType.MANUALLY) {
                mJavaScript.locationWithCenter(longitude, latitude);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private boolean checkPermission() {
        List<String> deniedList = new ArrayList<>(permissionArray.length);
        for (String per : permissionArray) {
            if (!EasyPermissions.hasPermissions(this, per) && PermissionChecker.checkSelfPermission(this, per) != PermissionChecker.PERMISSION_GRANTED) {
                deniedList.add(per);
            }
        }
        if (deniedList.isEmpty()) {
            return true;
        }

        Log.d(TAG, "checkPermission: 请求权限");
        EasyPermissions.requestPermissions(this,  100, deniedList.toArray(new String[]{}));
        return false;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, requestCode + "onPermissionsDenied: " + perms.toString());
        // TODO: 2017/9/10  权限拒绝处理
    }

    @Override
    public void onLoadFinished() {
        if(mErrorLayout.getVisibility() == View.VISIBLE){
            mErrorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadError() {
        mErrorLayout.setVisibility(View.VISIBLE);
    }
}
