package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.MessageManager;
import com.huixiang.xzb.chatserver.manager.UserManager;
import com.huixiang.xzb.chatserver.proto.SMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextMessageHandler extends SimpleChannelInboundHandler<TextWebSocketFrame>
{
    private static final Logger logger = LoggerFactory.getLogger(TextMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg)
            throws Exception
    {
        //TODO
        //check sessionkey and uid in every message cycle

        logger.info("channelRead0: {}",msg.text());
        //TODO send message to destination
        //If destination online
        ctx.channel().writeAndFlush(null);
        //If destination offline

        //send number of unresolved message
        //{ type: "sys", code: 100, mess: unresolved number}
//        Integer number = MessageManager.getUnresolvedNum(uid);
//        SMessage sMessage = new SMessage("sys", 100, number.toString());
//        send(ctx, sMessage);
    }

}
