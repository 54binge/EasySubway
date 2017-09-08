package com.binge.easysubway;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017/9/1.
 */

public class MyJavaScript {
    private WebView mWebView;
    private Handler mHandler;
    private ValueCallback mValueCallback;

    public MyJavaScript(Handler handler) {
        mHandler = handler;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public void setValueCallback(ValueCallback valueCallback) {
        mValueCallback = valueCallback;
    }

    public void getLines() {
        mWebView.evaluateJavascript("javascript:getLines()", null);
    }

    public void showCities() {
        mWebView.evaluateJavascript("javascript:showCities()", null);
    }

    public void selectLine(String lineId) {
        mWebView.evaluateJavascript("javascript:selectLine(" + lineId + ")", null);
    }

    public void setStartStation(String stationId) {
        mWebView.evaluateJavascript("javascript:setStartStation(" + stationId + ")", null);
    }

    public void setEndStation(String stationId) {
        mWebView.evaluateJavascript("javascript:setEndStation(" + stationId + ")", null);
    }

    public void locationOnResume(String longitude, String latitude) {
        mWebView.evaluateJavascript("javascript:locationOnResume(" + longitude + "," + latitude + ")", null);
    }

    public void locationWithCenter(String longitude, String latitude) {
        mWebView.evaluateJavascript("javascript:locationWithCenter(" + longitude + "," + latitude + ")", null);
    }

    @JavascriptInterface
    public void onLoadingCompleted() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mValueCallback.onReceiveValue(ConstantValue.REQUEST_CODE_LOADING_COMPLETED, null);
            }
        });
    }

    /*
    * [
  {
    "name": "1号线",
    "laname": "",
    "color": "E47878",
    "id": "110100023110",
    "status": "1",
    "shortname": "地铁1号线"
  }
]*/
    @JavascriptInterface
    public void returnLinesData(final String data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mValueCallback.onReceiveValue(ConstantValue.REQUEST_CODE_GET_LINES, data);
            }
        });

    }

    @JavascriptInterface
    public void returnCitiesData(final String data) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mValueCallback.onReceiveValue(ConstantValue.REQUEST_CODE_GET_CITIES, data);
            }
        });

    }
}
