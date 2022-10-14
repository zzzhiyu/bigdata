package com.yzy.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

public class NioSelectorServer02 {

    public static void main(String[] args) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(9999));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 从Selector中获取事件（客户端连接、客户端发送数据……），如果没有事件发生，会阻塞
            int count = selector.select();
            System.out.println(count);
            Set<SelectionKey> selectionKeys = selector.selectedKeys(); //
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 有客户端请求建立连接
                if (selectionKey.isAcceptable()) {
                    handleAccept(selectionKey);
                }
                // 有客户端发送数据
                else if (selectionKey.isReadable()) {
                    handleRead(selectionKey);
                }
                // select 在事件发生后，就会将相关的 key 放入 Selector 中的 selectedKeys 集合，但不会在处理完后从 selectedKeys 集合中移除，需要我们自己手动删除
                iterator.remove();
            }
        }
    }

    private static void handleAccept(SelectionKey selectionKey) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        if (Objects.nonNull(socketChannel)) {
            // 设置客户端Channel为非阻塞模式，否则在执行socketChannel.read()时会阻塞
            socketChannel.configureBlocking(false);
            Selector selector = selectionKey.selector();
            socketChannel.register(selector, SelectionKey.OP_READ);
        }
    }

    private static void handleRead(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(8);
        int length = socketChannel.read(readBuffer);
        if (length > 0) {
            System.out.println("receive msg:" + new String(readBuffer.array(), 0, length, "UTF-8"));
        } else if (length == -1) {
            socketChannel.close();
            return;
        }
    }

}

