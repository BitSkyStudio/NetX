package com.github.industrialcraft.test;

import com.github.industrialcraft.test.proto.TeleportEntity;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TestClientProcessor extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof TeleportEntity.TeleportEntityMessage tpMsg){
            System.out.println(tpMsg);
        }
    }
}
