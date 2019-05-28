package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;
/**
 * server send SMessage to clients
 * datetime: long timestamp, setter when necessary
 */
public class SMessage {
    private String type;
    private int code;
    private String mess;
    private Long datetime;

    public SMessage() {
    }

    public SMessage(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public SMessage(String type, int code, String mess) {
        this.type = type;
        this.code = code;
        this.mess = mess;
    }

    public SMessage(String type, String mess) {
        this.type = type;
        this.mess = mess;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }
}
