package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;

public class OutMessage {
    private String type;
    private int code;
    private String mess;
    private String datetime;

    public OutMessage(String type, int code,String datetime) {
        this.type = type;
        this.code = code;
        this.datetime=datetime;
    }

    public OutMessage(String type, int code, String mess,String datetime) {
        this.type = type;
        this.code = code;
        this.mess = mess;
        this.datetime=datetime;
    }

    public OutMessage(String type, String mess,String datetime) {
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
