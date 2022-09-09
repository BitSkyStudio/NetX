package com.github.industrialcraft.netx;

public abstract class ServerMessage {
    public abstract void visit(Visitor visitor);

    public static class Connect extends ServerMessage {
        private SocketUser user;
        public Connect(SocketUser user) {
            this.user = user;
        }
        public SocketUser getUser() {
            return user;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.connect(user);
        }
    }
    public static class Disconnect extends ServerMessage {
        private SocketUser user;
        public Disconnect(SocketUser user) {
            this.user = user;
        }
        public SocketUser getUser() {
            return user;
        }

        @Override
        public void visit(Visitor visitor) {
            visitor.disconnect(user);
        }
    }
    public static class IncomingMessage extends ServerMessage {
        private SocketUser user;
        private Object msg;
        public IncomingMessage(SocketUser user, Object msg) {
            this.user = user;
            this.msg = msg;
        }
        public SocketUser getUser() {
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
    public static class Exception extends ServerMessage {
        private SocketUser user;
        private Throwable exception;
        public Exception(SocketUser user, Throwable exception) {
            this.user = user;
            this.exception = exception;
        }
        public SocketUser getUser() {
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
        default void connect(SocketUser user){}
        default void disconnect(SocketUser user){}
        default void message(SocketUser user, Object msg){}
        default void exception(SocketUser user, Throwable exception){
            exception.printStackTrace();
            if(user != null)
                user.disconnect();
        }
    }
}
