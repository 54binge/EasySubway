package com.binge.easysubway

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.binge.easysubway.SimpleAdapter.OnItemClickListener
import com.binge.easysubway.widget.Fab
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gordonwong.materialsheetfab.MaterialSheetFab
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.find

class MainActivity : AppCompatActivity(), ValueCallback, OnItemClickListener<Line> {

    var mJS: MyJavaScript? = null
    var mMaterialDialog: MaterialSheetFab<Fab>? = null
    var mDataList: List<Line> = ArrayList()
    var mAdapter: SimpleAdapter<Line>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mWebView.loadUrl("file:///android_asset/subway.html")
        mJS = MyJavaScript(Handler())
        mJS!!.setWebView(mWebView.webView)
        mWebView.addJavascriptInterface(mJS!!, "MyJavaScript")

        mJS!!.setValueCallback(this)

        val fabColor = ContextCompat.getColor(this, R.color.fab_color)
        val dialogColor = ContextCompat.getColor(this, R.color.dialog_bg_color)
        mMaterialDialog = MaterialSheetFab(mShowLineBtn, mFabSheet, mOverlay, dialogColor, fabColor)

        mMaterialDialog!!.setEventListener(object : MaterialSheetFabEventListener() {
            override fun onShowSheet() {
                mJS!!.getLines()
            }
/*
            override fun onHideSheet() {
                super.onHideSheet()
            }

            override fun onSheetHidden() {
                super.onSheetHidden()
            }

            override fun onSheetShown() {
                super.onSheetShown()
            }*/
        })


        mAdapter = object : SimpleAdapter<Line>(R.layout.layout_item_line, mDataList) {
            override fun onBindViewData(holder: RecyclerView.ViewHolder, line: Line) {
                holder.itemView.find<View>(R.id.mLineIcon).backgroundColor = Color.parseColor("#" + line.color)
                holder.itemView.find<TextView>(R.id.mLineName).text = line.name
            }
        }

        mAdapter!!.setOnItemClickListener(this)

        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.adapter = mAdapter

        mLocation.setOnClickListener { locationNow() }
    }

    override fun onBackPressed() {
        if (mMaterialDialog!!.isSheetVisible) {
            mMaterialDialog!!.hideSheet()
        } else {
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onReceiveValue(requestCode: String, s: String) {
        println("收到的data: " + s)
        val type = object : TypeToken<List<Line>>() {}.type
        val list = Gson().fromJson<List<Line>>(s, type)
        println("---->" + list.size)
        mAdapter!!.updateData(list)
    }

    override fun onItemClick(t: Line?) {
        mMaterialDialog!!.hideSheet()
        mJS!!.selectLine(t!!.id)
    }

    fun locationNow() {
        val coordinate = CoordinateManager.getInstance().coordinate
        println(coordinate[0] + "  " + coordinate[1])
        mJS!!.locationNow(coordinate[0], coordinate[1])
    }
}
