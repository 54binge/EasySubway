package com.binge.easysubway;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Binge on 2017/9/4.
 */

public class CoordinateManager {
    private static final String TAG = "CoordinateManager";
    private Context mContext;
    private static CoordinateManager manager;
    private LocationManager mLocationManager;

    public static CoordinateManager getInstance() {
        if (manager == null) {
            manager = new CoordinateManager();
        }
        return manager;
    }

    public void init(Context context) {
        mContext = context;
        if (mContext != null) {
            mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        }
    }


    public String[] getCoordinate() {
        String[] str = new String[2];
        if (mLocationManager != null) {
            String bestProvider = mLocationManager.getBestProvider(getCriteria(), true);
//            String bestProvider = getBestProvider(mLocationManager);
            Log.d(TAG, "getCoordinate: " + bestProvider);
            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                // TODO: 2017/9/4 请求权限
            } else {
                if(TextUtils.isDigitsOnly(bestProvider)){
                    Toast.makeText(mContext, "没有找到可用的定位传感器", Toast.LENGTH_SHORT).show();
                    return str;
                }
                if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    Toast.makeText(mContext, "打开GPS可以提高定位精度哦", Toast.LENGTH_SHORT).show();
                }

//                mLocationManager.requestSingleUpdate(bestProvider, this, );
                Location lastKnownLocation = mLocationManager.getLastKnownLocation(bestProvider);
                if(lastKnownLocation != null){
                    str[0] = String.valueOf(lastKnownLocation.getLongitude());
                    str[1] = String.valueOf(lastKnownLocation.getLatitude());
                }
            }

        }
        return str;
    }

    private String getBestProvider(@NotNull LocationManager locationManager) {
        String provider = null;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = locationManager.getProvider(LocationManager.GPS_PROVIDER).getName();
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = locationManager.getProvider(LocationManager.NETWORK_PROVIDER).getName();
        } else if (locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            provider = locationManager.getProvider(LocationManager.PASSIVE_PROVIDER).getName();
        }
        return provider;
    }

    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// 高精度
        criteria.setAltitudeRequired(false);// 海拔要求
        criteria.setBearingRequired(true);// 方位要求
        criteria.setCostAllowed(true);// 允许有花费
        criteria.setPowerRequirement(Criteria.POWER_HIGH);// 低功耗

        return criteria;
    }
}
