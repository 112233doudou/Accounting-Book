package com.example.zuoye;

import android.app.Application;

import com.example.zuoye.db.DBManager;

/*表示全局应用的类*/
public class UniteAPP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        DBManager.initDB(getApplicationContext());
    }
}
