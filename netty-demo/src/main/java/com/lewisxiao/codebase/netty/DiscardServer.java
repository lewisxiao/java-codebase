package com.lewisxiao.codebase.netty;

import com.lewisxiao.codebase.netty.handler.DiscardServerHandler;
import com.lewisxiao.codebase.netty.handler.TimeEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run () {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)

                // 装配子通道流水线，
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    // 有新的连接到达时会创建一个通道的子通道，并初始化
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        // 这里可以管理子通道中的handler处理器
                        // 此处向子通道中添加了一个handler处理器
                        ch.pipeline().addLast(new TimeEncoder(), new DiscardServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        try {
            // 绑定端口，开始接收进来的连接
            ChannelFuture future = bootstrap.bind(port).sync();

            // 等待服务器 socket 关闭 。
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        int port = 8099;
        new DiscardServer(port).run();
    }
}
