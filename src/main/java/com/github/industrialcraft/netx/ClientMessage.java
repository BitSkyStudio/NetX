package com.github.industrialcraft.netx;

public abstract class ClientMessage {
    public abstract void visit(Visitor visitor);

    public static class Connect extends ClientMessage {
        private NetXClient user;
        public Connect(NetXClient user) {
            this.user = user;
        }
        public NetXClient getUser() {
            return user;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.connect(user);
        }
    }
    public static class Disconnect extends ClientMessage {
        private NetXClient user;
        public Disconnect(NetXClient user) {
            this.user = user;
        }
        public NetXClient getUser() {
            return user;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.disconnect(user);
        }
    }
    public static class IncomingMessage extends ClientMessage {
        private NetXClient user;
        private Object msg;
        public IncomingMessage(NetXClient user, Object msg) {
            this.user = user;
            this.msg = msg;
        }
        public NetXClient getUser() {
            return user;
        }
        public Object getMessage() {
            return msg;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.message(user, msg);
        }
    }
    public static class Exception extends ClientMessage {
        private NetXClient user;
        private Throwable exception;
        public Exception(NetXClient user, Throwable exception) {
            this.user = user;
            this.exception = exception;
        }
        public NetXClient getUser() {
            return user;
        }
        public Throwable getException() {
            return exception;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.exception(user, exception);
        }
    }

    public interface Visitor{
        default void connect(NetXClient user){}
        default void disconnect(NetXClient user){}
        default void message(NetXClient user, Object msg){}
        default void exception(NetXClient user, Throwable exception){
            exception.printStackTrace();
            user.disconnect();
        }
    }
}
