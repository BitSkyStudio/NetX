package com.github.industrialcraft.netx;

import com.github.industrialcraft.netx.timeout.PingMessageEncoder;
import com.github.industrialcraft.netx.timeout.TimeOutHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

public class NetXClient extends Thread{
    String host;
    int port;
    int readTimeout;
    int writeTimeout;
    int maxLength;
    MessageRegistry registry;
    NetXClient.ChannelInit channelInit;
    public NetXClient(String host, int port, MessageRegistry registry, NetXClient.ChannelInit channelInit) {
        this.host = host;
        this.port = port;
        this.readTimeout = 30;
        this.writeTimeout = 5;
        this.maxLength = Integer.MAX_VALUE;
        this.registry = registry;
        this.channelInit = channelInit;
    }
    public void setTimout(int readTimeout, int writeTimeout){
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }
    public void setMaxFrameLength(int maxLength){
        this.maxLength = maxLength;
    }
    @Override
    public void run() {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(maxLength, 0, 4, 0, 4));
                    ch.pipeline().addLast(new LengthFieldPrepender(4));
                    ch.pipeline().addLast(new MessageDecoder(registry));
                    ch.pipeline().addLast(new IdleStateHandler(readTimeout, writeTimeout, 0));
                    ch.pipeline().addLast(new PingMessageEncoder());
                    ch.pipeline().addLast(new TimeOutHandler());
                    ch.pipeline().addLast(new MessageEncoder(registry));
                    if(channelInit != null)
                        channelInit.onChannelInit(ch);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public interface ChannelInit{
        void onChannelInit(SocketChannel channel);
    }
}
