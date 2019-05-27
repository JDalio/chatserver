package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;

/**
 * client send CMessage to server
 * from type and mess is necessary
 */
public class CMessage {
    private String from;
    private String to;
    private String type;
    private String mess;
    private Long datetime;

    public CMessage() {
    }

    public CMessage(String msg) {
        JSONObject obj = JSONObject.parseObject(msg);
        this.from = obj.getString("from");
        this.type = obj.getString("type");
        this.mess = obj.getString("mess");
        if(obj.containsKey("datetime")){
            this.datetime=obj.getLong("datetime");
        }
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

    public Long getDatetime() {
        return datetime;
    }

    public void setDatetime(Long datetime) {
        this.datetime = datetime;
    }
}
