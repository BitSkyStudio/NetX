package com.github.industrialcraft.netx;

import io.netty.channel.Channel;

public class SocketUser {
    Object userData;
    Channel channel;
    public SocketUser(Channel channel) {
        this.userData = null;
        this.channel = channel;
    }
    public Object getUserData() {
        return userData;
    }
    public void setUserData(Object userData) {
        this.userData = userData;
    }
    public void send(Object msg){
        send(msg, false);
    }
    public void send(Object msg, boolean flush){
        if(flush)
            channel.writeAndFlush(msg);
        else
            channel.write(msg);
    }
    public void disconnect(){
        channel.close();
    }
}
