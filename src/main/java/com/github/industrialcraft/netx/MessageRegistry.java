package com.github.industrialcraft.netx;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBufInputStream;

import java.io.IOError;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MessageRegistry {
    private HashMap<Integer,MessageDescriptor> messages;
    public MessageRegistry() {
        this.messages = new HashMap<>();
    }
    public void register(int id, MessageDescriptor descriptor){
        if(id == 0)
            throw new RuntimeException("id 0 is reserved for ping packet");
        if(messages.containsKey(id))
            throw new RuntimeException("id " + id + " already registered");
        messages.put(id, descriptor);
    }
    public MessageDescriptor byID(int id){
        return messages.get(id);
    }
    public int toID(Class clazz){
        for(Map.Entry<Integer,MessageDescriptor> entry : messages.entrySet()){
            if(entry.getValue().clazz == clazz)
                return entry.getKey();
        }
        return -1;
    }

    public static class MessageDescriptor{
        Class clazz;
        MessageParser parser;
        public MessageDescriptor(Class clazz, MessageParser parser) {
            this.clazz = clazz;
            this.parser = parser;
        }
    }
    public interface MessageParser {
        GeneratedMessageV3 read(ByteBufInputStream stream) throws IOException;
    }
}
