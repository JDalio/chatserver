package com.huixiang.xzb.chatserver.Server;

import com.huixiang.xzb.chatserver.configuration.Configuration;
import com.huixiang.xzb.chatserver.handler.UserStateHandler;
import com.huixiang.xzb.chatserver.handler.BinaryMessageHandler;
import com.huixiang.xzb.chatserver.handler.TextMessageHandler;
import com.huixiang.xzb.chatserver.manager.UserManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatServer {

    protected Logger logger = LoggerFactory.getLogger(ChatServer.class);

    private int port= Configuration.PORT;
    private int bossThreadNumber=Configuration.BOSS_THREAD_NUMBER;
    private int scanDuration = Configuration.SCAN_DURATION;
    private int pingDuration = Configuration.PING_DURATION;
    private int readIdleDuration = Configuration.READ_IDLE_DURATION;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workGroup;
    private ServerBootstrap bootstrap;

    private ScheduledExecutorService deamonService;

    public ChatServer() {
        bossGroup = new NioEventLoopGroup(bossThreadNumber);
        workGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(),
                                new HttpObjectAggregator(65536),
                                new ChunkedWriteHandler(),
                                new WebSocketServerCompressionHandler(),
                                new WebSocketServerProtocolHandler("/p2p",null,true,10485760),
                                new IdleStateHandler(readIdleDuration,0,0),
                                new UserStateHandler(),
                                new TextMessageHandler(),
                                new BinaryMessageHandler()
                        );
                    }
                });
        deamonService = Executors.newScheduledThreadPool(Configuration.DEAMON_THREAD_NUMBER);
    }

    public void start() {

        try {
            //start the server
            ChannelFuture cf = bootstrap.bind(port).sync();

            // 定时扫描所有的Channel，关闭open but inActive的Channel
            deamonService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    logger.info("scanNotActiveChannel --------");
                    UserManager.scanNotActiveChannel();
                }
            }, 3, scanDuration, TimeUnit.SECONDS);

            // send Ping message periodically(15 /s)
//            deamonService.scheduleAtFixedRate(()->{
//                logger.info("Broadcast Ping ---------");
//                UserManager.broadCastPing();
//            },3,pingDuration,TimeUnit.SECONDS);

            logger.info("WebSocketServer start success, port is:{}", port);
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("WebSocketServer start fail,", e);
        }
    }

    //TODO redis persistence
    public void shutdown() {
        deamonService.shutdown();
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
