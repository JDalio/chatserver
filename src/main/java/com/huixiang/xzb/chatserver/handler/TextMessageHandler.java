package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.MessageManager;
import com.huixiang.xzb.chatserver.manager.UserManager;
import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.proto.SMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextMessageHandler {
    private static final Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);
    private final CMessage msg;
    private final ChannelHandlerContext ctx;

    public TextMessageHandler(ChannelHandlerContext ctx, CMessage msg) {
        this.msg = msg;
        this.ctx = ctx;
    }

    public void process() {
        if (msg.getType().equals("txt")) {
            //cache message
            MessageManager.cache(msg);

            //If destination online
            //send message to destination
            Channel channel = UserManager.getChannel(msg.getTo());
            if (channel != null) {
                // convert message
                msg.setSessionkey(null);
                msg.setTo(null);
                channel.writeAndFlush(new TextWebSocketFrame(msg.toString()));
            }
        } else if (msg.getType().equals("ack")) {
            MessageManager.ackCMessage(msg);
        }


    }
}
