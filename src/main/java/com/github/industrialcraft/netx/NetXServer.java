package com.github.industrialcraft.netx;

import com.github.industrialcraft.netx.timeout.PingMessageEncoder;
import com.github.industrialcraft.netx.timeout.TimeOutHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetXServer extends Thread{
    int port;
    int readTimeout;
    int writeTimeout;
    int maxLength;
    MessageRegistry registry;
    ConcurrentLinkedQueue<ServerMessage> messageQueue;
    ArrayList<SocketUser> users;
    public NetXServer(int port, MessageRegistry registry) {
        this.port = port;
        this.readTimeout = 30;
        this.writeTimeout = 5;
        this.maxLength = Integer.MAX_VALUE;
        this.registry = registry;
        this.messageQueue = new ConcurrentLinkedQueue<>();
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
        NetXServer server = this;

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(maxLength, 0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new MessageDecoder(registry));
                            ch.pipeline().addLast(new IdleStateHandler(readTimeout, writeTimeout, 0));
                            ch.pipeline().addLast(new PingMessageEncoder());
                            ch.pipeline().addLast(new TimeOutHandler());
                            ch.pipeline().addLast(new MessageEncoder(registry));
                            ch.pipeline().addLast(new ServerProcessor(server));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
    public interface ChannelInit{
        void onChannelInit(SocketChannel channel);
    }
    public ConcurrentLinkedQueue<ServerMessage> getQueue() {
        return messageQueue;
    }
    public List<SocketUser> getUsers() {
        return Collections.unmodifiableList(users);
    }
}
