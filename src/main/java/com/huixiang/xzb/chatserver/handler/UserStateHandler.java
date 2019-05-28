package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.MessageManager;
import com.huixiang.xzb.chatserver.manager.UserManager;
import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.proto.SMessage;
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
        logger.info("channelRead0: {}", msg.text());
        CMessage inmsg = new CMessage(msg.text());
        if (inmsg.getType().equals("sys")) {
            String mess = inmsg.getMess();
            //register event
            if (mess.equals("register")) {
                //TODO check uid
                UserManager.addUser(inmsg.getFrom(), ctx.channel());
                //send number of unresolved message
                //{ type: "sys", code: 100, mess: unresolved number}
                Integer number = MessageManager.getUnresolvedNum(inmsg.getFrom());
                SMessage sMessage = new SMessage("sys", 100, number.toString());
                ctx.writeAndFlush(new TextWebSocketFrame(sMessage.toString()));
            }
            //ping message
            else if (mess.equals("ping")) {
                logger.info("ping uid: {}", inmsg.getFrom());
                ctx.writeAndFlush(new TextWebSocketFrame(new SMessage("sys", 1000).toString()));

            }
            //TODO
            //send previous messages
        } else {
            //chat message
            ctx.fireChannelRead(msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("channelInactive");
        UserManager.delUser(ctx.channel());
        super.channelInactive(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        logger.info("userEventTriggered: " + evt.getClass().getSimpleName());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 判断Channel是否读空闲, 读空闲时移除Channel
            if (event.state().equals(IdleState.READER_IDLE)) {
                logger.warn("ChannelRead Timeout: [{}]", ctx.channel().remoteAddress());
                UserManager.delUser(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            ctx.writeAndFlush(new TextWebSocketFrame(new SMessage("sys", 200).toString()));
        } else {
            ctx.fireUserEventTriggered(evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("exceptionCaught", cause);
        UserManager.delUser(ctx.channel());
    }
}
