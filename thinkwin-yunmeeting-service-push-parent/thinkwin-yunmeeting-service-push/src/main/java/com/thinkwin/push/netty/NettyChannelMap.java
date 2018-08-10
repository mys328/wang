package com.thinkwin.push.netty;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理SocketChannel的Map<terminalId,channel>
 */
public class NettyChannelMap {
    //private
    public static Map<String, SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();

    public static void add(String clientId, SocketChannel socketChannel) {
        map.put(clientId, socketChannel);
    }

    public static Channel get(String clientId) {
        return map.get(clientId);
    }

    public static String remove(SocketChannel socketChannel) {
        String key = "";
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == socketChannel) {
                key = entry.getKey().toString();
                map.remove(entry.getKey());
            }
        }
        return key;
    }

}
