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

    public void showLines() {
        mWebView.evaluateJavascript("javascript:showLines()", null);
    }

    public void showCitys(){
        mWebView.evaluateJavascript("javascript:showCitys()", null);
    }

    public void selectLine(String lineId){
        mWebView.evaluateJavascript("javascript:selectLine("+ lineId +")", null);
    }

    public void setStartStation(String stationId){
        mWebView.evaluateJavascript("javascript:setStartStation("+ stationId +")", null);
    }

    public void setEndStation(String stationId){
        mWebView.evaluateJavascript("javascript:setEndStation("+ stationId +")", null);
    }

    public void location(){
        // TODO: 2017/9/2 定位
    }

    @JavascriptInterface
    public void returnLinesData(String data) {
        mValueCallback.onReceiveValue(ConstantValue.REQUEST_CODE_GET_LINES, data);
    }

    @JavascriptInterface
    public void returnCitysData(String data) {
        mValueCallback.onReceiveValue(ConstantValue.REQUEST_CODE_GET_CITYS, data);
    }
}
