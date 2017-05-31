package com.xushuai.ceyan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.bw.xlistview.XListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements XListView.IXListViewListener {
    private AlertDialog.Builder b;
    private AlertDialog.Builder builder;
    private XListView lv;
    int page = 5;
    private List<Beans.AppBean> app = new ArrayList<>();
    private OffLineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        get(page);
        lv = (XListView) findViewById(R.id.lv);
        lv.setPullRefreshEnable(true);
        lv.setPullLoadEnable(true);
        lv.setXListViewListener(this);
        adapter = new OffLineAdapter(app, this);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setDialog(position);
                builder.show();
            }
        });
    }

    private void get(int page) {
        RequestParams params = new RequestParams("http://mapp.qzone.qq.com/cgi-bin/mapp/mapp_subcatelist_qq?yyb_cateid=-10&categoryName=%E8%85%BE%E8%AE%AF%E8%BD%AF%E4%BB%B6&pageNo=" + page + "&pageSize=20&type=app&platform=touch&network_type=unknown&resolution=412x732");
        x.http().get(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                //解析result
                Log.d("ChannelActivity", "请求成功");
                String substring = result.substring(0, result.length() - 1);
                Gson gson = new Gson();
                Beans beans = gson.fromJson(substring, Beans.class);
                List<Beans.AppBean> list = beans.getApp();
                app.addAll(list);
                adapter.notifyDataSetChanged();
            }

            //请求异常后的回调方法
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            //主动调用取消请求的回调方法
            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }

    private void download(String url) {
        RequestParams params = new RequestParams(url);
        //自定义保存路径，Environment.getExternalStorageDirectory()：SD卡的根目录
        params.setSaveFilePath(Environment.getExternalStorageDirectory() + "/myapp/");
        //自动为文件命名
        params.setAutoRename(true);
        x.http().post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                //apk下载完成后，调用系统的安装方法
                Log.d("ChannelActivity", "下载成功");
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
                MainActivity.this.startActivity(intent);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }

            //网络请求之前回调
            @Override
            public void onWaiting() {
            }

            //网络请求开始的时候回调
            @Override
            public void onStarted() {
            }

            //下载的时候不断回调的方法
            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                //当前进度和文件总大小
                Log.d("JAVA", "current：" + current + "，total：" + total);
            }
        });
    }

    public void setDialog(final int position) {
        b = new AlertDialog.Builder(this);
        b.setTitle("版本更新");
        b.setMessage("现在检测到新版本，是否更新?");
        b.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                download(app.get(position).getUrl());
            }
        });
        final String[] itmes = {"wifi", "手机流量"};
        builder = new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle("网络选择");
        builder.setSingleChoiceItems(itmes, -1, new AlertDialog.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    b.show();
                    dialog.cancel();
                } else {
                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        page -= 1;
        app.clear();
        get(page);
        lv.stopRefresh();
        lv.setRefreshTime("刚刚");
    }

    @Override
    public void onLoadMore() {
        page += 1;
        get(page);
        lv.stopLoadMore();
    }
}