package com.github.industrialcraft.test;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.netx.NetXClient;
import com.github.industrialcraft.netx.NetXServer;
import com.github.industrialcraft.netx.ServerMessage;
import com.github.industrialcraft.test.proto.TeleportEntity;

public class TestMain {
    public static void main(String args[]){
        MessageRegistry registry = new MessageRegistry();
        registry.register(1, new MessageRegistry.MessageDescriptor(TeleportEntity.TeleportEntityMessage.class, stream -> TeleportEntity.TeleportEntityMessage.parseFrom(stream), (obj, stream) -> ((TeleportEntity.TeleportEntityMessage)obj).writeTo(stream)));
        NetXServer server = new NetXServer(1234, registry);
        server.start();
        NetXClient client = new NetXClient("localhost", 1234, registry, channel -> channel.pipeline().addLast(new TestClientProcessor()));
        client.start();
        while (true){
            ServerMessage message = server.getQueue().poll();
            if(message != null){
                if(message instanceof ServerMessage.IncomingMessage incoming){
                    System.out.println(incoming.getMessage());
                } else
                    System.out.println(message);
            }
        }
    }
}
