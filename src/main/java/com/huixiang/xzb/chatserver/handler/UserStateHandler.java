package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.UserManager;
import com.huixiang.xzb.chatserver.proto.InMessage;
import com.huixiang.xzb.chatserver.proto.OutMessage;
import com.huixiang.xzb.chatserver.utils.DateTimeUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * establish connection -> use redis to do auth
 */
public class UserStateHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private static final Logger logger = LoggerFactory.getLogger(UserStateHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        InMessage inmsg = new InMessage(msg.text());
        if (inmsg.getTo() == null) {
            //register event
            if (inmsg.getType().equals("sys") && inmsg.getMess().equals("register")) {
                UserManager.addUser(inmsg.getFrom(), ctx.channel());
            }
            //TODO
            //send previous messages
        } else {
            //chat message
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 判断Channel是否读空闲, 读空闲时移除Channel
            if (event.state().equals(IdleState.READER_IDLE)) {
                logger.warn("userEventTriggered: ChannelRead Timeout: [{}]", ctx.channel().remoteAddress());
                UserManager.delUser(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.writeAndFlush(new TextWebSocketFrame(new OutMessage("sys", 200, DateTimeUtil.getCurrentTime()).toString()));
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }
}
