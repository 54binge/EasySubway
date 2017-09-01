package com.binge.easysubway

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.webkit.ValueCallback
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity(), ValueCallback<String> {
    override fun onReceiveValue(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        println(p0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mWebView.loadUrl("file:///android_asset/subway.html")
        val mJS = MyJavaScript(Handler())
        mJS.setWebView(mWebView.webView)
        mWebView.addJavascriptInterface(mJS, mJS.javaClass.name)
        mShowLineBtn.onClick {
            mJS.showLines()
        }

        mJS.setValueCallback(this)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
