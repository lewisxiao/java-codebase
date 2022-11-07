package com.lewisxiao.codebase.netty.handler;

import com.lewisxiao.codebase.netty.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * channelActive() 方法将会在连接被建立并且准备进行通信时被调用
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        ByteBuf time = ctx.alloc().buffer(4);
//        time.writeInt((int) (System.currentTimeMillis() / 1000 + 2208988800L));

        // 重点：netty中所有操作都是异步的，不会立即发生，不要指望代码会顺序执行
        ChannelFuture future = ctx.writeAndFlush(new UnixTime());

        // 添加一个监听器，异步操作完成后会调用监听器中的逻辑
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                assert  future == channelFuture;
                ctx.close();
            }
        });
    }
}