package com.huixiang.xzb.chatserver.manager;

import com.huixiang.xzb.chatserver.configuration.Configuration;
import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.proto.SMessage;
import com.huixiang.xzb.chatserver.util.RedisPool;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<String, Channel> userChannels = new ConcurrentHashMap<>();

    private static final Jedis jedis = RedisPool.getJedis();

    public static boolean checkUser(String uid) {
        String db = Configuration.AUTHORITY_DB;
        if(!jedis.exists(db) || jedis.hget(db, uid) == null){
            return false;
        }
        if (Integer.valueOf(jedis.hget(db, uid)) > 0) {
            return true;
        }else{
            // del member
            jedis.hdel(db,uid);
        }
        return false;
    }

    public static boolean addUser(String uid, Channel channel) {
        if (!channel.isActive()) {
            logger.error("addUser: channel is not active, uid {}", uid);
            return false;
        }
        logger.info("addUser: add user {}", uid);
        rwLock.writeLock().lock();
        userChannels.put(uid, channel);
        rwLock.writeLock().unlock();
        return true;
    }

    public static void delUser(Channel channel) {
        try {
            rwLock.writeLock().lock();
            for (String uid : userChannels.keySet()) {
                if (userChannels.get(uid) == channel) {
                    logger.info("delUser: uid {}", uid);
                    userChannels.remove(uid);
                }
            }
            if (channel != null) {
                channel.disconnect();
                channel.close();
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static boolean isOnline(String uid) {
        return userChannels.containsKey(uid);
    }

    public static int getOnlineUserNumber() {
        return userChannels.size();
    }

    /**
     * scan and close in-active Channel
     */
    public static void scanNotActiveChannel() {
        for (Channel ch : userChannels.values()) {
            if (!ch.isOpen() || !ch.isActive()) {
                delUser(ch);
            }
        }
        logger.info(">>>>>>Online Number: {}", userChannels.size());
    }

//    public static void broadCastPing() {
//        for (Channel ch : userChannels.values()) {
//            if (!ch.isOpen() || !ch.isActive()) {
//                delUser(ch);
//            } else {
//                ch.writeAndFlush(new TextWebSocketFrame(new SMessage("sys", 1000).toString()));
//            }
//        }
//    }
}
