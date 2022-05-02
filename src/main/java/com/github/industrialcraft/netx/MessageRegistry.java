package com.github.industrialcraft.netx;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;

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
        descriptor.setId(id);
    }
    public MessageDescriptor byID(int id){
        return messages.get(id);
    }
    public MessageDescriptor byClass(Class clazz){
        for(Map.Entry<Integer,MessageDescriptor> entry : messages.entrySet()){
            if(entry.getValue().clazz == clazz)
                return entry.getValue();
        }
        return null;
    }

    public static class MessageDescriptor{
        private int id;
        public final Class clazz;
        public final MessageReader reader;
        public final MessageWriter writer;
        public MessageDescriptor(Class clazz, MessageReader reader, MessageWriter writer) {
            this.clazz = clazz;
            this.reader = reader;
            this.writer = writer;
        }
        private void setId(int id) {
            this.id = id;
        }
        public int getId() {
            return id;
        }
    }
    public interface MessageReader {
        Object read(ByteBufInputStream stream) throws IOException;
    }
    public interface MessageWriter {
        void write(Object obj, ByteBufOutputStream stream) throws IOException;
    }
}
