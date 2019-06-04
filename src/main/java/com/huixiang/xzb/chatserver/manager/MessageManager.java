package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.util.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageManager {
    private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

    private static final Jedis jedis = RedisPool.getJedis();

    public static boolean checkCMessage(CMessage msg) {
        // check session
        if (msg.getType().equals("sys") && msg.getMess().equals("ping")) {
            return true;
        }
        // check whether sessionkey and uid match
        String uid = jedis.get("session:" + msg.getSessionkey());
        if (uid == null || !uid.equals(msg.getFrom())) {
            return false;
        }
        // check whether 'from' could talk to 'to'
        if (msg.getTo() != null) {
            String key1 = String.join(":", "chat", msg.getFrom(), msg.getTo());
            String key2 = String.join(":", "chat", msg.getTo(), msg.getFrom());
            if (jedis.get(key1) == null && jedis.get(key2) == null) {
                return false;
            }
        }
        return true;
    }

    public static void cache(CMessage cMessage) {
        String key = "cache:" + cMessage.getTo();
        Long score = cMessage.getDatetime();

        String member = cMessage.getFrom() + ":" + cMessage.getType() + ":" + cMessage.getDatetime() + " " + cMessage.getMess();
        jedis.zadd(key, score, member);
    }

    public static int getUnresolvedUids(String uid) {
        return jedis.zcard("cache:" + uid).intValue();
    }

    public static List<CMessage> getUnresolvedMsg(String uid) {
        List<CMessage> msgs = new ArrayList<>();
        Set<String> strs = jedis.zrevrange("cache:" + uid, 0, -1);
        for (String str : strs) {
            String[] arr = str.split("[: ]", 4);
            CMessage msg = new CMessage();
            msg.setFrom(arr[0]);
            msg.setTo(uid);
            msg.setType(arr[1]);
            msg.setDatetime(Long.valueOf(arr[2]));
            msg.setMess(arr[3]);
            msgs.add(msg);
        }
        return msgs;
    }

    public static void ackCMessage(CMessage msg) {
        String key = "cache:" + msg.getFrom();
        Long score = msg.getDatetime();
        String uid = msg.getTo();

        Set<String> messages = jedis.zrangeByScore(key, score, score);
        for (String message : messages) {
            String[] arr = message.split(":", 2);
            if (arr[0].equals(uid)) {
                jedis.zrem(key, message);
                break;
            }
        }
    }

}
