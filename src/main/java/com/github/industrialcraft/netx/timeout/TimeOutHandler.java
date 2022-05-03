package com.github.industrialcraft.netx.timeout;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class TimeOutHandler extends ChannelDuplexHandler {
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt instanceof IdleStateEvent){
        	IdleStateEvent event = (IdleStateEvent) evt;
            if(event.state() == IdleState.READER_IDLE){
                ctx.fireExceptionCaught(new TimeoutException("reader idle"));
            } else if(event.state() == IdleState.WRITER_IDLE){
                ctx.writeAndFlush(new PingMessage());
            }
        }
    }

}
