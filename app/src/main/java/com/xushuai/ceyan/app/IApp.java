package com.xushuai.ceyan.app;

import android.app.Application;

import org.xutils.x;

public class IApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
    }
}