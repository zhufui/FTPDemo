package com.my.ftpdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018.03.28.
 */

public class FtpService extends Service {
    public static final String TAG = FtpService.class.getSimpleName();

    private FtpServer server;
    private String user = "1";
    private String password = "1";
    private static String rootPath;
    private int port = 2221;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.d(TAG, "rootPath = " + rootPath);

        try {
            init();
            Toast.makeText(this, "启动ftp服务成功", Toast.LENGTH_SHORT).show();
        } catch (FtpException e) {
            e.printStackTrace();
            Toast.makeText(this, "启动ftp服务失败", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        Toast.makeText(this, "关闭ftp服务", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化
     *
     * @throws FtpException
     */
    public void init() throws FtpException {
        release();
        startFtp();
    }

    private void startFtp() throws FtpException {
        FtpServerFactory serverFactory = new FtpServerFactory();

        //设置访问用户名和密码还有共享路径
        BaseUser baseUser = new BaseUser();
        baseUser.setName(user);
        baseUser.setPassword(password);//如果不设置密码，那就是匿名登录
        baseUser.setHomeDirectory(rootPath);

        List<Authority> authorities = new ArrayList<Authority>();
        authorities.add(new WritePermission());
        baseUser.setAuthorities(authorities);
        serverFactory.getUserManager().save(baseUser);


        ListenerFactory factory = new ListenerFactory();
        factory.setPort(port); //设置端口号 非ROOT不可使用1024以下的端口
        serverFactory.addListener("default", factory.createListener());

        server = serverFactory.createServer();
        server.start();
    }


    /**
     * 释放资源
     */
    public void release() {
        stopFtp();
    }

    private void stopFtp() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }

}
