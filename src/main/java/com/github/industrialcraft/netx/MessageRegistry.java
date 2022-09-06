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
    private HashMap<Class,MessageDescriptor> clazzToMessages;
    public MessageRegistry() {
        this.messages = new HashMap<>();
        this.clazzToMessages = new HashMap<>();
    }
    public void register(int id, MessageDescriptor descriptor){
        if(id == 0)
            throw new RuntimeException("id 0 is reserved for ping packet");
        if(messages.containsKey(id))
            throw new RuntimeException("id " + id + " already registered");
        if(clazzToMessages.containsKey(descriptor.clazz))
            throw new RuntimeException("class " + descriptor.clazz + " already registered");
        messages.put(id, descriptor);
        clazzToMessages.put(descriptor.clazz, descriptor);
        descriptor.setId(id);
    }
    public MessageDescriptor byID(int id){
        return messages.get(id);
    }
    public MessageDescriptor byClass(Class clazz){
        return clazzToMessages.get(clazz);
    }

    public static class MessageDescriptor<T>{
        private int id;
        public final Class<T> clazz;
        public final MessageReader<T> reader;
        public final MessageWriter<T> writer;
        public MessageDescriptor(Class<T> clazz, MessageReader<T> reader, MessageWriter<T> writer) {
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
    public interface MessageReader<T> {
        T read(ByteBufInputStream stream) throws IOException;
    }
    public interface MessageWriter<T> {
        void write(T obj, ByteBufOutputStream stream) throws IOException;
    }
}
