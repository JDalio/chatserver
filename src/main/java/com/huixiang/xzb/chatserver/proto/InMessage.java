package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

public class InMessage {
    private String from;
    private String to;
    private String type;
    private String mess;
    private String datetime;

    public InMessage() {
    }

    public InMessage(String msg) {
        JSONObject obj = JSONObject.parseObject(msg);
        this.from = obj.getString("from");
        this.type = obj.getString("type");
        this.mess = obj.getString("mess");
        this.datetime=obj.getString("datetime");
        if (obj.containsKey("to")) {
            this.to = obj.getString("to");
        }
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
