package com.github.industrialcraft.netx;

public abstract class ServerMessage {
    public static class Connect extends ServerMessage {
        private SocketUser user;
        public Connect(SocketUser user) {
            this.user = user;
        }
        public SocketUser getUser() {
            return user;
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
    }
}
