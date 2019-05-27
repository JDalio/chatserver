package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.util.DateTimeUtil;
import com.huixiang.xzb.chatserver.util.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;

/**
 * When send messages to a offline user, I call the offline user first,
 * then save the messages in redis set.
 * <p>
 * When the user go online, send these messages saved previous to him first
 */
public class MessageManager {
    private static final Jedis jedis = RedisPool.getJedis();

    public static List<CMessage> getMsgs(String uid) {
        if (uid.length() != 6 || jedis.llen(uid) == 0L) {
            return null;
        }
        List<String> strs = jedis.lrange(uid, 0, -1);
        List<CMessage> cmsgs = new ArrayList<>();
        for (String str : strs) {
            CMessage cMessage = new CMessage();
            cMessage.setTo(uid);
            String[] ary = str.split("[: ]", 4);
            cMessage.setFrom(ary[0]);
            cMessage.setType(ary[1]);
            cMessage.setDatetime(Long.valueOf(ary[2]));
            cMessage.setMess(ary[3]);
            cmsgs.add(cMessage);
        }
        return cmsgs;
    }

}
