package com.huixiang.xzb.chatserver.manager;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserManager {
    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<String, Channel> userChannels = new ConcurrentHashMap<>();

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
                    channel.close();
                }
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public static boolean isOnline(String uid){
        return userChannels.containsKey(uid);
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
    }
}
