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

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<String, Channel> userChannels = new ConcurrentHashMap<>();

    private static final Jedis jedis = RedisPool.getJedis();

    public static String getUserId(String sessionkey) {
        return jedis.get("session:" + sessionkey);
    }

    public static Channel getChannel(String uid) {
        return userChannels.get(uid);
    }

    public static boolean checkConnectAuthority(String sessionkey) {
        // check sessionkey -> get uid
        String uid = jedis.get("session:" + sessionkey);
        if (uid == null) {
            return false;
        }
        // whether user have others to chat
        String chatAuthDB = Configuration.AUTHORITY_DB;
        String value = jedis.hget(chatAuthDB, uid);
        if (!jedis.exists(chatAuthDB) || value == null) {
            return false;
        }
        if (Integer.valueOf(value) > 0) {
            return true;
        } else {
            // del member
            jedis.hdel(chatAuthDB, uid);
        }
        return false;
    }

    public static boolean addUser(String uid, Channel channel) {
        if (!channel.isActive()) {
            logger.error("addUser: channel is not active, uid {}", uid);
            return false;
        }
        logger.debug("addUser: add user {}", uid);
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
                    logger.debug("delUser: uid {}", uid);
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

    public static void sendUnconfirmedMessage() {
        for (String uid : userChannels.keySet()) {
            List<CMessage> messages = MessageManager.getUnresolvedMsg(uid);
            for (CMessage message : messages) {
                userChannels.get(uid).writeAndFlush(new TextWebSocketFrame(message.toString()));
            }
        }
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
}
