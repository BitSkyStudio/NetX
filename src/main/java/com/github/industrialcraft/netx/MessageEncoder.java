package com.github.industrialcraft.netx;

import com.github.industrialcraft.netx.timeout.PingMessage;
import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.DataOutputStream;

public class MessageEncoder extends MessageToByteEncoder<Object> {
    private MessageRegistry registry;
    public MessageEncoder(MessageRegistry registry) {
        this.registry = registry;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof PingMessage){
            out.writeInt(0);
            return;
        }
        MessageRegistry.MessageDescriptor md = registry.byClass(msg.getClass());
        if(md == null){
            ctx.fireExceptionCaught(new RuntimeException("attempting to serialize message with no assigned id, class: " + msg.getClass().getSimpleName()));
            return;
        }
        if(md.writer == null){
            ctx.fireExceptionCaught(new RuntimeException("trying to send packet without writer implemented, class: " + msg.getClass().getSimpleName()));
            return;
        }
        out.writeInt(md.getId());
        md.writer.write(msg, new DataOutputStream(new ByteBufOutputStream(out)));
    }
}
