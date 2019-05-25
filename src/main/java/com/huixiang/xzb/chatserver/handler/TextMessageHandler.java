package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.UserManager;
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
        logger.info("channelRead0: {}",msg.text());
        //TODO send message to destination
        //If destination online
        ctx.channel().writeAndFlush(null);
        //If destination offline
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        logger.error("exceptionCaught", cause);
        UserManager.delUser(ctx.channel());
    }

}
