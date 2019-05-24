package com.huixiang.xzb.chatserver.entity;

import io.netty.channel.Channel;

import java.util.concurrent.atomic.AtomicInteger;


public class UserInfo
{
    private static AtomicInteger uidGener = new AtomicInteger(1000);

    private boolean isAuth = false; // 是否认证
    private long time = 0;  // 登录时间
    private int userId;     // UID
    private String nick;    // 昵称
    private String addr;    // 地址
    private Channel channel;// 通道
}
