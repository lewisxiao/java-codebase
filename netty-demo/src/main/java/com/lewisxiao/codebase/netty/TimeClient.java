package com.lewisxiao.codebase.netty;

import com.lewisxiao.codebase.netty.handler.TimeClientHandler;
import com.lewisxiao.codebase.netty.handler.TimeDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8099;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        Bootstrap bootStrap = new Bootstrap();
        bootStrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new TimeDecoder(), new TimeClientHandler());
                    }
                });

        try {
            // 启动客户端
            ChannelFuture future = bootStrap.connect(host, port).sync();

            // 等待连接关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
