package com.github.industrialcraft.netx;

import com.github.industrialcraft.netx.timeout.PingMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ClientProcessor extends ChannelInboundHandlerAdapter {
    private NetXClient client;
    public ClientProcessor(NetXClient client) {
        this.client = client;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        client.setClientChannel(ctx.channel());
        client.addToMessageQueue(new ClientMessage.Connect(client));
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.setClientChannel(null);
        client.addToMessageQueue(new ClientMessage.Disconnect(client));
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PingMessage)
            return;
        client.addToMessageQueue(new ClientMessage.IncomingMessage(client, msg));
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        client.addToMessageQueue(new ClientMessage.Exception(client, cause));
    }
}
