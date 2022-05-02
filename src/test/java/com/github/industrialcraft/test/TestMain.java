package com.github.industrialcraft.test;

import com.github.industrialcraft.netx.*;
import com.github.industrialcraft.test.proto.TeleportEntity;

public class TestMain {
    public static void main(String args[]){
        MessageRegistry registry = new MessageRegistry();
        registry.register(1, new MessageRegistry.MessageDescriptor(TeleportEntity.TeleportEntityMessage.class, stream -> TeleportEntity.TeleportEntityMessage.parseFrom(stream), (obj, stream) -> ((TeleportEntity.TeleportEntityMessage)obj).writeTo(stream)));
        NetXServer server = new NetXServer(1234, registry);
        server.start();
        NetXClient client = new NetXClient("localhost", 1234, registry);
        client.start();

        ServerMessage.Visitor serverVisitor = new ServerMessage.Visitor() {
            @Override
            public void message(SocketUser user, Object msg) {
                System.out.println("server: " + msg);
                user.send(TeleportEntity.TeleportEntityMessage.newBuilder().setId(1).setX(2).setY(3).setZ(4).build());
            }

            @Override
            public void disconnect(SocketUser user) {
                System.out.println("server disconnect");
            }
        };
        ClientMessage.Visitor clientVisitor = new ClientMessage.Visitor() {
            @Override
            public void connect(NetXClient user) {
                user.send(TeleportEntity.TeleportEntityMessage.newBuilder().setId(1).setX(2).setY(3).setZ(4).build());
            }

            @Override
            public void message(NetXClient user, Object msg) {
                System.out.println("client: " + msg);
                user.disconnect();
            }

            @Override
            public void disconnect(NetXClient user) {
                System.out.println("client disconnect");
            }
        };
        while (true){
            server.visitMessage(serverVisitor);
            client.visitMessage(clientVisitor);
        }
    }
}
