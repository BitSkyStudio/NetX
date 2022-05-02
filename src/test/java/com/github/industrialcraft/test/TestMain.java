package com.github.industrialcraft.test;

import com.github.industrialcraft.netx.*;
import com.github.industrialcraft.test.proto.TeleportEntity;

public class TestMain {
    public static void main(String args[]){
        MessageRegistry registry = new MessageRegistry();
        registry.register(1, new MessageRegistry.MessageDescriptor(TeleportEntity.TeleportEntityMessage.class, stream -> TeleportEntity.TeleportEntityMessage.parseFrom(stream), (obj, stream) -> ((TeleportEntity.TeleportEntityMessage)obj).writeTo(stream)));
        NetXServer server = new NetXServer(1234, registry);
        server.start();
        NetXClient client = new NetXClient("localhost", 1234, registry, channel -> channel.pipeline().addLast(new TestClientProcessor()));
        client.start();

        ServerMessage.Visitor visitor = new ServerMessage.Visitor() {
            @Override
            public void message(SocketUser user, Object msg) {
                System.out.println(msg);
            }
        };
        while (true){
            server.visitMessage(visitor);
        }
    }
}
