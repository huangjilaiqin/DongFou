package com.lessask.dongfou.net;

import android.util.Log;

import com.lessask.dongfou.Config;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by huangji on 2015/10/16.
 */
public class LASocketIO {

    private static String TAG = LASocketIO.class.getSimpleName();
    private static String wshost = Config.host;

    public static Socket getSocket(){
        return LazyHolder.socket;
    }
    private static Socket newSocket(){
        Socket socket = null;
        try {
            IO.Options options = new IO.Options();
            options.transports = new String[]{"websocket", "polling"};
            options.path = Config.wsPath;
            socket = IO.socket(wshost, options);
            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, onTimeout);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_RECONNECT, onReconnect);
            socket.on(Socket.EVENT_ERROR, onError);
            socket.connect();
            Log.d(TAG, "connect to "+wshost);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return socket;
    }
    private static class LazyHolder {
        private static final Socket socket= newSocket();
    }
    private static Emitter.Listener onConnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onConnect");
		}
	};
    private static Emitter.Listener onConnectError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.e(TAG, "onConnectError:" + error);
		}
	};
    private static Emitter.Listener onDisconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onDisconnect");
		}
	};
    private static Emitter.Listener onTimeout = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onTimeout:" + args[0]);
		}
	};
    private static Emitter.Listener onReconnect = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            Log.e(TAG, "onReconnect");
            Log.e(TAG, "args length:" + args.length);
		}
	};
    private static Emitter.Listener onError = new Emitter.Listener() {
		@Override
		public void call(final Object... args) {// 监控回调
            String error= "";
            for(int i=0;i<args.length;i++)
                error+=args[i]+", ";
            Log.e(TAG, "onError:" + error);
		}
	};
}
