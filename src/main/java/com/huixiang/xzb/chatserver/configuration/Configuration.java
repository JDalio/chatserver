package com.huixiang.xzb.chatserver.configuration;

public class Configuration {
    //websocket port
    public static final int PORT = 9898;

    //netty configuration
    public static final int BOSS_THREAD_NUMBER = 1;
    public static final int SCAN_DURATION = 60;
    public static final int READ_IDLE_DURATION = 300;

    //redis configuration
    public static final String REDIS_HOST = "localhost";
    public static final int REDIS_PORT = 6379;
    public static final String REDIS_USERNAME = "dalio";
    public static final String REDIS_PASSWD = "lg280137!";
    public static final int REDIS_MAX_TOTAL=4;
    public static final int REDIS_MAX_IDLE=4;
}
