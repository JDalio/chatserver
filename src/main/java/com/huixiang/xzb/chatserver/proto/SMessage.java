package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;
/**
 * server message
 */
public class SMessage {
    private String type;
    private int code;
    private String mess;
    private String datetime;

    public SMessage(String type, int code, String datetime) {
        this.type = type;
        this.code = code;
        this.datetime=datetime;
    }

    public SMessage(String type, int code, String mess, String datetime) {
        this.type = type;
        this.code = code;
        this.mess = mess;
        this.datetime=datetime;
    }

    public SMessage(String type, String mess, String datetime) {
        this.type = type;
        this.mess = mess;
        this.datetime=datetime;
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

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
