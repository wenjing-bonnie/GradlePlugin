package com.wj.gradle.plugin;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

import com.wj.gradle.plugin.annotation.WjRouter;

import androidx.annotation.Nullable;

@WjRouter(key = "/main.html?key=999")
public class MainActivity extends Activity {
    private TextView tvLongText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("", getApplication().toString());
        String info = null;
        //LogUtils.logV("" + info.toString());
        tvLongText= findViewById(R.id.tv_long_text);
        tvLongText.setText("11");
        Html.fromHtml("11");
    }
}