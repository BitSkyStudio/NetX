package com.github.industrialcraft.netx;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MessageEncoder extends MessageToByteEncoder<GeneratedMessageV3> {
    MessageRegistry registry;
    public MessageEncoder(MessageRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, GeneratedMessageV3 msg, ByteBuf out) throws Exception {
        int id = registry.toID(msg.getClass());
        if(id == -1){
            ctx.fireExceptionCaught(new RuntimeException("attempting to serialize message with no assigned id"));
            return;
        }
        out.writeInt(id);
        msg.writeTo(new ByteBufOutputStream(out));
    }
}
