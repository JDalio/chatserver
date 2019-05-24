package com.huixiang.xzb.chatserver.Server;

import com.huixiang.xzb.chatserver.Configuration.Configuration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
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
    private int

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
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(
                                new HttpServerCodec(),
                                new HttpObjectAggregator(65536),
                                new ChunkedWriteHandler(),
                                new IdleStateHandler(60, 0, 0),
                                new AuthHandler(),
                                new MessageHandler()
                        );
                    }
                });
        deamonService = Executors.newScheduledThreadPool(2);
    }

    public void start() {

        try {
            //start the server
            ChannelFuture cf = bootstrap.bind(port).sync();

            // 定时扫描所有的Channel，关闭失效的Channel
            deamonService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    logger.info("scanNotActiveChannel --------");
                    UserInfoManager.scanNotActiveChannel();
                }
            }, 3, 60, TimeUnit.SECONDS);

            // 定时向所有客户端发送Ping消息
            deamonService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    logger.info("broadCastPing --------");
                    UserInfoManager.broadCastPing();
                }
            }, 3, 50, TimeUnit.SECONDS);

            logger.info("WebSocketServer start success, port is:{}", port);
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("WebSocketServer start fail,", e);
        }
    }

    public void shutdown() {
        deamonService.shutdown();
        bossGroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }
}
