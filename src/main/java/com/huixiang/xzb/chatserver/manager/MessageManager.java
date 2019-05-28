package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.util.DateTimeUtil;
import com.huixiang.xzb.chatserver.util.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * When send messages to a offline user, I call the offline user first,
 * then save the messages in redis set.
 * <p>
 * When the user go online, send these messages saved previous to him first
 */
public class MessageManager {
    private static final Jedis jedis = RedisPool.getJedis();

    public static void cache(String msg) {
        CMessage cMessage = new CMessage(msg);
        String key = cMessage.getTo();
        Long score = cMessage.getDatetime();
        String member = cMessage.getFrom() + ":" + cMessage.getType() + ":" + cMessage.getDatetime() + " " + cMessage.getMess();
        jedis.zadd(key, score, member);
    }

    public static int getUnresolvedNum(String uid) {
        return jedis.zcard(uid).intValue();
    }

    public static List<CMessage> readAll(String uid) {
        List<CMessage> msgs = new ArrayList<>();
        Set<String> strs = jedis.zrange(uid, 0, -1);
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
