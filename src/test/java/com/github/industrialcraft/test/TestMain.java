package com.github.industrialcraft.test;

import com.github.industrialcraft.netx.MessageRegistry;
import com.github.industrialcraft.netx.NetXClient;
import com.github.industrialcraft.netx.NetXServer;
import com.github.industrialcraft.test.proto.TeleportEntity;

public class TestMain {
    public static void main(String args[]){
        MessageRegistry registry = new MessageRegistry();
        registry.register(1, new MessageRegistry.MessageDescriptor(TeleportEntity.TeleportEntityMessage.class, stream -> TeleportEntity.TeleportEntityMessage.parseFrom(stream)));
        NetXServer server = new NetXServer(1234, registry, channel -> channel.pipeline().addLast(new TestProcessor()));
        server.start();
        NetXClient client = new NetXClient("localhost", 1234, registry, channel -> channel.pipeline().addLast(new TestClientProcessor()));
        client.run();
    }
}
