package com.github.industrialcraft.netx;

import com.github.industrialcraft.netx.timeout.PingMessageEncoder;
import com.github.industrialcraft.netx.timeout.TimeOutHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetXClient extends Thread{
    String host;
    int port;
    int readTimeout;
    int writeTimeout;
    int maxLength;
    MessageRegistry registry;
    Channel clientChannel;
    ConcurrentLinkedQueue<ClientMessage> messageQueue;
    public NetXClient(String host, int port, MessageRegistry registry) {
        this.host = host;
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
        NetXClient client = this;

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
                    ch.pipeline().addLast(new ClientProcessor(client));
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
    void addToMessageQueue(ClientMessage msg){
        messageQueue.add(msg);
    }
    public ClientMessage pollMessage(){
        return messageQueue.poll();
    }
    public void visitMessage(ClientMessage.Visitor visitor){
        ClientMessage message = pollMessage();
        if(message != null)
            message.visit(visitor);
    }
    void setClientChannel(Channel channel){
        this.clientChannel = channel;
    }
    public void send(Object msg){
        send(msg, false);
    }
    public void send(Object msg, boolean flush){
        if(flush)
            clientChannel.writeAndFlush(msg);
        else
            clientChannel.write(msg);
    }
    public void disconnect(){
        clientChannel.close();
    }
}
