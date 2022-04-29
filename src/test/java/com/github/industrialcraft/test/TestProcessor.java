package com.github.industrialcraft.test;

import com.github.industrialcraft.test.proto.TeleportEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestProcessor extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(TeleportEntity.TeleportEntityMessage.newBuilder().setId(1).setX(2).setY(3).setZ(4).build());
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }
}
