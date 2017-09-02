package com.binge.easysubway

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class MainActivity : AppCompatActivity(), ValueCallback {
    override fun onReceiveValue(requestCode: String, s: String) {
        /*
        * [
  {
    "name": "1号线",
    "laname": "",
    "color": "E47878",
    "id": "110100023110",
    "status": "1",
    "shortname": "地铁1号线"
  },
  {
    "name": "2号线",
    "laname": "",
    "color": "5591CE",
    "id": "110100023098",
    "status": "1",
    "shortname": "地铁2号线"
  }
]
        * */
        println("收到的data: " + s)
        val type = object : TypeToken<List<Map<String, String>>>() {}.type
        val lineList = Gson().fromJson<List<Map<String, String>>>(s, type)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mWebView.loadUrl("file:///android_asset/subway.html")
        val mJS = MyJavaScript(Handler())
        mJS.setWebView(mWebView.webView)
        mWebView.addJavascriptInterface(mJS, "MyJavaScript")
        mShowLineBtn.onClick {
            mJS.showLines()
        }

        mJS.setValueCallback(this)
    }


    override fun onDestroy() {
        super.onDestroy()
    }

}
