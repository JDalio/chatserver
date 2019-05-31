package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.util.DateTimeUtil;
import com.huixiang.xzb.chatserver.util.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MessageManager {
    private static final Jedis jedis = RedisPool.getJedis();

    public static void cache(String msg) {
        CMessage cMessage = new CMessage(msg);
        String key = "cache:" + cMessage.getTo();
        Long score = cMessage.getDatetime();
        String member = cMessage.getFrom() + ":" + cMessage.getType() + ":" + " " + cMessage.getMess();
        jedis.zadd(key, score, member);
    }

    public static int getUnresolvedNum(String uid) {
        return jedis.zcard("cache:" + uid).intValue();
    }

    public static List<CMessage> readAll(String uid) {
        List<CMessage> msgs = new ArrayList<>();
        Set<String> strs = jedis.zrange("cache:" + uid, 0, -1);
        for (String str : strs) {
            Long datetime = jedis.zscore("cache:" + uid, str).longValue();
            String[] arr = str.split("[: ]", 3);
            CMessage msg = new CMessage();
            msg.setFrom(arr[0]);
            msg.setTo(uid);
            msg.setType(arr[1]);
            msg.setMess(arr[2]);
            msg.setDatetime(datetime);
            msgs.add(msg);
        }
        return msgs;
    }
//
//    public static List<CMessage> getMsgs(String uid) {
//        if (uid.length() != 6 || jedis.llen(uid) == 0L) {
//            return null;
//        }
//        List<String> strs = jedis.lrange(uid, 0, -1);
//        List<CMessage> cmsgs = new ArrayList<>();
//        for (String str : strs) {
//            CMessage cMessage = new CMessage();
//            cMessage.setTo(uid);
//            String[] ary = str.split("[: ]", 4);
//            cMessage.setFrom(ary[0]);
//            cMessage.setType(ary[1]);
//            cMessage.setDatetime(Long.valueOf(ary[2]));
//            cMessage.setMess(ary[3]);
//            cmsgs.add(cMessage);
//        }
//        return cmsgs;
//    }

}
