package com.huixiang.xzb.chatserver.manager;

import com.mda.chat.entity.UserInfo;
import com.mda.chat.proto.Message;
import com.mda.chat.utils.BlankUtil;
import com.mda.chat.utils.NettyUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserInfoManager
{
    private static final Logger logger = LoggerFactory.getLogger(UserInfoManager.class);

    private static ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);

    private static ConcurrentMap<Channel, UserInfo> userInfos = new ConcurrentHashMap<>();
    private static AtomicInteger userCount = new AtomicInteger(0);

    public static void addChannel(Channel channel)
    {
        String remoteAddr = NettyUtil.parseChannelRemoteAddr(channel);
        if (!channel.isActive())
        {
            logger.error("channel is not active, address: {}", remoteAddr);
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setAddr(remoteAddr);
        userInfo.setChannel(channel);
        userInfo.setTime(System.currentTimeMillis());
        userInfos.put(channel, userInfo);
    }

    // change the nick name
    public static boolean saveUser(Channel channel, String nick) {
        UserInfo userInfo = userInfos.get(channel);
        if (userInfo == null) {
            return false;
        }
        if (!channel.isActive()) {
            logger.error("channel is not active, address: {}, nick: {}", userInfo.getAddr(), nick);
            return false;
        }
        // 增加一个认证用户
        userCount.incrementAndGet();
        userInfo.setNick(nick);
        userInfo.setAuth(true);
        userInfo.setUserId();
        userInfo.setTime(System.currentTimeMillis());
        return true;
    }

    /**
     * 从缓存中移除Channel，并且关闭Channel
     *
     * @param channel
     */
    public static void removeChannel(Channel channel)
    {
        try
        {
            logger.warn("Timeout: User Channel Will Be Remove, Address :{}", NettyUtil.parseChannelRemoteAddr(channel));
            rwLock.writeLock().lock();
            channel.close();
            UserInfo userInfo = userInfos.get(channel);
            if (userInfo != null)
            {
                UserInfo tmp = userInfos.remove(channel);
                if (tmp != null && tmp.isAuth())
                {
                    // 减去一个认证用户
                    userCount.decrementAndGet();
                }
            }
        }
        finally
        {
            rwLock.writeLock().unlock();
        }

    }

    /**
     * 广播普通消息
     *
     * @param message
     */
    public static void broadcastMess(int uid, String nick, String message)
    {
        if (!BlankUtil.isBlank(message))
        {
            try
            {
                rwLock.readLock().lock();
                Set<Channel> keySet = userInfos.keySet();
                for (Channel ch : keySet)
                {
                    UserInfo userInfo = userInfos.get(ch);
                    if (userInfo == null || !userInfo.isAuth()) continue;
                    ch.writeAndFlush(new TextWebSocketFrame(Message.buildMessProto(uid, nick, message)));
                }
            }
            finally
            {
                rwLock.readLock().unlock();
            }
        }
    }

    /**
     * 广播系统消息
     */
    public static void broadCastInfo(int code, Object mess) {
        try {
            rwLock.readLock().lock();
            Set<Channel> keySet = userInfos.keySet();
            for (Channel ch : keySet) {
                UserInfo userInfo = userInfos.get(ch);
                if (userInfo == null || !userInfo.isAuth())
                    continue;
                ch.writeAndFlush(new TextWebSocketFrame(Message.buildSystProto(code, mess)));
            }
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    public static void broadCastPing()
    {
        try
        {
            rwLock.readLock().lock();
            logger.info("broadCastPing userCount: {}", userCount.intValue());
            Set<Channel> keySet = userInfos.keySet();
            for (Channel ch : keySet)
            {
                UserInfo userInfo = userInfos.get(ch);
                if (userInfo == null || !userInfo.isAuth()) continue;
                ch.writeAndFlush(new TextWebSocketFrame(Message.buildPingProto()));
            }
        }
        finally
        {
            rwLock.readLock().unlock();
        }
    }

    /**
     * 发送系统消息
     */
    public static void sendInfo(Channel channel, int code, Object mess)
    {
        channel.writeAndFlush(new TextWebSocketFrame(Message.buildSystProto(code, mess)));
    }

    public static void sendPong(Channel channel)
    {
        channel.writeAndFlush(new TextWebSocketFrame(Message.buildPongProto()));
    }

    /**
     * 扫描并关闭失效的Channel
     */
    public static void scanNotActiveChannel()
    {
        Set<Channel> keySet = userInfos.keySet();
        for (Channel ch : keySet)
        {
            UserInfo userInfo = userInfos.get(ch);
            if (userInfo == null) continue;
            if (!ch.isOpen() || !ch.isActive() || (!userInfo.isAuth() &&
                    (System.currentTimeMillis() - userInfo.getTime()) > 10000))
            {
                removeChannel(ch);
            }
        }
    }


    public static UserInfo getUserInfo(Channel channel)
    {
        return userInfos.get(channel);
    }

    public static ConcurrentMap<Channel, UserInfo> getUserInfos()
    {
        return userInfos;
    }

    public static int getAuthUserCount()
    {
        return userCount.get();
    }

    public static void updateUserTime(Channel channel)
    {
        UserInfo userInfo = getUserInfo(channel);
        if (userInfo != null)
        {
            userInfo.setTime(System.currentTimeMillis());
        }
    }
}
