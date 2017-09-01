package com.binge.easysubway;

import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

/**
 * Created by Administrator on 2017/9/1.
 */

public class MyJavaScript {
    private WebView mWebView;
    private Handler mHandler;
    private ValueCallback<String> mValueCallback;

    public MyJavaScript(Handler handler) {
        mHandler = handler;
    }

    public void setWebView(WebView webView) {
        mWebView = webView;
    }

    public void setValueCallback(ValueCallback<String> valueCallback){
        mValueCallback = valueCallback;
    }

    public void showLines() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.evaluateJavascript("javascript:callJS()", mValueCallback);
            }
        });
    }

}
