package com.huixiang.xzb.chatserver.proto;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * type: message type
 * kind: sub message type
 * mess: message content
 */
public class Message {
    private int type;
    private Map<String, Object> body;

    public Message(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }

    public static String buildPingProto() {
        return new Message(MessageType.PING).toString();
    }

    public static String buildPongProto() {
        return new Message(MessageType.PONG).toString();
    }

    public static String buildSysProto(int kind, Object mess) {
        Message msg = new Message(MessageType.SYS);
        msg.body = new HashMap<>();
        msg.body.put("kind", kind);
        msg.body.put("mess", mess);
        return msg.toString();
    }

    public static String buildAuthProto(boolean isSuccess) {
        Message msg = new Message(MessageType.AUTH);
        msg.body = new HashMap<>();
        msg.body.put("king", MessageType.AUTH_STATE);
        msg.body.put("mess", isSuccess);
        return msg.toString();
    }

    public static String buildMessProto(Map<String, Object> mess) {
        Message msg = new Message(MessageType.MESS);
        msg.setBody(mess);
        return msg.toString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Object> getBody() {
        return body;
    }

    public void setBody(Map<String, Object> body) {
        this.body = body;
    }
}
