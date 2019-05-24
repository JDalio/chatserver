package com.huixiang.xzb.chatserver.proto;

/**
 * Describe type of message
 */
public class MessageType {
    public static final int PING = 10015;            //ping message
    public static final int PONG = 10016;            //pong message

    public static final int SYS = 20000;             //system message
    public static final int SYS_USER_COUNT = 20001;  //active user count
    public static final int SYS_EROR = 29000;        //system user message

    public static final int AUTH = 1000;             //auth message
    public static final int AUTH_STATE = 1001;       //auth state

    public static final int MESS = 2000;             //user message
}
