package com.binge.easysubway.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import static android.content.ContentValues.TAG;

/**
 * Created by Administrator on 2017/9/1.
 */

public class LoadingWebView extends RelativeLayout {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private LoadingStateListener mLoadingStateListener;
    private boolean isLoadingError = false;

    public LoadingWebView(Context context) {
        super(context);
        initUI(context);
    }

    public LoadingWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public LoadingWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
    }

    private void initUI(Context context) {
        mWebView = new WebView(context);
        mWebView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mProgressBar = new ProgressBar(context, null, android.R.attr.progressBarStyle);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT);
        mProgressBar.setLayoutParams(params);

        addView(mWebView);
        addView(mProgressBar);

        setupWebView();
    }

    private void setupWebView() {
        if (mWebView == null) {
            return;
        }
        WebSettings webSettings = mWebView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        webSettings.setUserAgentString("Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_2 like Mac OS X; zh-cn) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8H7 Safari/6533.18.5");

        //解决图片不显示
        webSettings.setBlockNetworkImage(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

        //缩放操作
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件

        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
//        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setAllowFileAccess(false); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式

//        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebClient());

        if (isAboveVersionHONEYCOMB()) {
            mWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
        }
        if (Build.VERSION.SDK_INT > 10 && Build.VERSION.SDK_INT < 17) {
            mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        }
    }

    public class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            Log.d(TAG, "onProgressChanged: ------->加载完毕>>>" + newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(GONE);
            } else {
                if (mProgressBar.getVisibility() == GONE) {
                    mProgressBar.setVisibility(VISIBLE);
                }
                mProgressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    private class MyWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            isLoadingError = false;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(GONE);
            if (mLoadingStateListener != null) {
                if (isLoadingError) {
                    mLoadingStateListener.onLoadError();
                } else {
                    mLoadingStateListener.onLoadFinished();

                }
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            isLoadingError = true;
        }
    }

    public void setLoadingStateListener(LoadingStateListener loadingStateListener) {
        if (mWebView != null) {
            mLoadingStateListener = loadingStateListener;
        }
    }

    public void loadUrl(String url) {
        mWebView.loadUrl(url);
    }

    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object object, String name) {
        if (mWebView != null && object != null && !TextUtils.isEmpty(name)) {
            mWebView.addJavascriptInterface(object, name);
        }
    }

    public void reLoad() {
        mWebView.reload();
    }

    public boolean isAboveVersionHONEYCOMB() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return true;
        }
        return false;
    }

    public WebView getWebView() {
        return mWebView;
    }

    public interface LoadingStateListener {
        void onLoadFinished();

        void onLoadError();
    }
}
