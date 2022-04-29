package com.github.industrialcraft.netx.timeout;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class PingMessageEncoder extends MessageToByteEncoder<PingMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, PingMessage msg, ByteBuf out) throws Exception {
        out.writeInt(0);
    }
}
