package com.example.updateapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.version_update.version.UpdateActivity;
import com.example.version_update.version.VersionModel;

public class MainActivity extends AppCompatActivity {

    private VersionModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initModel();
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateActivity.launch(MainActivity.this, model);
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initModel() {
        model = new VersionModel();
        model.forced = 0;
        model.desc = "1:优化性能\n2:美化界面\n3:修复bug\n4:杀了一个产品祭天";
        model.url = "http://www.apk.anzhi.com/data3/apk/201703/14/4636d7fce23c9460587d602b9dc20714_88002100.apk";
        model.versionName = "测试demo";
        model.versionCode = 2;
    }
}
