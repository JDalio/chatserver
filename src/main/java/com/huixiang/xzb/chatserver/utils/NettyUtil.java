package com.huixiang.xzb.chatserver.utils;

import io.netty.channel.Channel;

import java.net.SocketAddress;

public class NettyUtil {

    /**
     * get remote ip of Channel
     *
     * @param channel
     * @return
     */
    public static String parseChannelRemoteAddr(final Channel channel) {
        if (channel == null) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }
            return addr;
        }

        return "";
    }
}
