package com.github.industrialcraft.netx;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class LanReceiver extends Thread{
    private boolean stop;
    private int port;
    private InetAddress address;
    private Consumer<LANMessage> handler;
    public LanReceiver(InetAddress address, int port, Consumer<LANMessage> handler) {
        this.stop = false;
        this.address = address;
        this.port = port;
        this.handler = handler;
    }

    public void run() {
        try {
            byte[] buf = new byte[1212];
            MulticastSocket socket = new MulticastSocket(port);
            socket.joinGroup(address);
            while (!stop) {
                try {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.setSoTimeout(100);
                    socket.receive(packet);
                    byte[] data = new byte[packet.getLength()];
                    System.arraycopy(packet.getData(), packet.getOffset(), data, 0, data.length);
                    handler.accept(new LANMessage(packet.getAddress(), packet.getPort(), new String(data, StandardCharsets.UTF_8)));
                } catch (SocketTimeoutException ex){}
            }
            socket.leaveGroup(address);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void cancel() {
        this.stop = true;
    }

    public static class LANMessage{
        private InetAddress address;
        private String content;
        private int port;
        public LANMessage(InetAddress address, int port, String content) {
            this.address = address;
            this.port = port;
            this.content = content;
        }
        public InetAddress getAddress() {
            return address;
        }
        public int getPort() {
            return port;
        }
        public String getContent() {
            return content;
        }
    }
}
