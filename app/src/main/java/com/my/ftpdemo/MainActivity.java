package com.my.ftpdemo;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import cn.com.zf.library.PermissionCallback;
import cn.com.zf.library.PermissionGroup;
import cn.com.zf.library.ZPermissions;

public class MainActivity extends AppCompatActivity implements PermissionCallback {

    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);

        ZPermissions.requestPermissions(MainActivity.this,
                PermissionReqCode.CODE0, MainActivity.this, PermissionGroup.build(
                        PermissionGroup.SD()
                ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, FtpService.class));
        ZPermissions.recycleCallback();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZPermissions.onRequestPermissionsResult(requestCode, grantResults);
    }

    public String getIp(){
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        return ip;
    }


    private String intToIp(int i) {

        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    @Override
    public void permissionGrant(int i) {
        String ip = getIp();
        if(TextUtils.isEmpty(ip)){
            textView.setText("获取不到IP，请连接网络");
        }else{
            String str = "请在IE浏览器上输入网址访问FTP服务\n" +
                    "ftp://"+ip+":2221\n" +
                    "账号:admin\n" +
                    "密码:123456";
            textView.setText(str);
        }

        startService(new Intent(this, FtpService.class));
    }

    @Override
    public void permissionDenied(int i) {

    }

}
