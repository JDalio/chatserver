package com.huixiang.xzb.chatserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BinaryMessageHandler extends SimpleChannelInboundHandler<BinaryWebSocketFrame> {
    private Logger logger= LoggerFactory.getLogger(BinaryMessageHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BinaryWebSocketFrame msg) throws Exception {
        logger.info("channelRead0");
        ByteBuf content = msg.content();
        int flag = content.readInt();
        //TODO save file and respond to client with the file url
//        ctx.writeAndFlush();
    }
}
