package com.huixiang.xzb.chatserver.handler;

import com.huixiang.xzb.chatserver.manager.MessageManager;
import com.huixiang.xzb.chatserver.manager.UserManager;
import com.huixiang.xzb.chatserver.proto.CMessage;
import com.huixiang.xzb.chatserver.proto.SMessage;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaders;
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

    private ChannelFuture send(ChannelHandlerContext ctx, Object msg) {
        return ctx.writeAndFlush(new TextWebSocketFrame(msg.toString()));
    }

    private void close(ChannelHandlerContext ctx) {
        ChannelFuture future = send(ctx, new SMessage("sys", 5000));
        future.addListener((ChannelFuture f) -> {
            ctx.channel().disconnect();
            ctx.channel().close();
        });
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        CMessage inmsg = new CMessage(msg.text());
        if(!inmsg.getMess().equals("ping")){
            logger.info("channelRead0: {}", msg.text());
        }
        //check CMessage
        if (!MessageManager.checkCMessage(inmsg)) {
            close(ctx);
            return;
        }

        if (inmsg.getType().equals("sys")) {
            String mess = inmsg.getMess();
            String uid = inmsg.getFrom();
            //ping message
            if (mess.equals("ping")) {
                send(ctx, new SMessage("sys", 1000));
            } else if (mess.equals("unread")) {
                Integer unread = MessageManager.getUnresolvedNum(uid);
                send(ctx, new SMessage("sys", 100, unread.toString()));
            }
            //TODO
            //send previous messages
        } else {
            //chat message
            new TextMessageHandler(ctx,inmsg).process();
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
//        logger.info("userEventTriggered: " + evt.getClass().getSimpleName());
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            // 判断Channel是否读空闲, 读空闲时移除Channel
            if (event.state().equals(IdleState.READER_IDLE)) {
//                logger.warn("ChannelRead Timeout: [{}]", ctx.channel().remoteAddress());
                UserManager.delUser(ctx.channel());
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            //read sessionkey in header
            WebSocketServerProtocolHandler.HandshakeComplete event = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            HttpHeaders headers = event.requestHeaders();
            String sessionkey = headers.get("sessionkey");
//            logger.info(headers.toString());
            if (sessionkey == null) {
                ctx.channel().disconnect();
                ctx.channel().close();
                return;
            }
            // do authority
            if (!UserManager.checkConnectAuthority(sessionkey)) {
                close(ctx);
                return;
            }

            //map user channel by uid
            String uid = UserManager.getUserId(sessionkey);
            UserManager.addUser(uid, ctx.channel());
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
