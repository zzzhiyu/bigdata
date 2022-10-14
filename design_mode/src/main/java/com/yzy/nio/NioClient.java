package com.yzy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class NioClient {
    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();
            boolean connect = socketChannel.connect(new InetSocketAddress(9999));
            System.out.println(connect);
            System.out.println("client connet success....");
            ByteBuffer writeBuffer = ByteBuffer.wrap("hello".getBytes(StandardCharsets.UTF_8));
            int write = socketChannel.write(writeBuffer);
            System.out.println(write);
            System.out.println("client send finished");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socketChannel != null) {
                try {
                    socketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
