package com.yzy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioSelectorServer {

    public static void main(String[] args) throws IOException {
        //打开监听套接字Channel，用来监听客户端连接
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //绑定端口号
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //设置为非阻塞（这是最重要的）
        serverSocketChannel.configureBlocking(false);
        //创建选择器
        Selector selector = Selector.open();
        //注册serverSocketChannel在selector中，
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);


        while (true) {
            if (selector.select(3000) == 0) {
                //若当前时间段（前3秒）没有一个事件发生，打印"没有客户端连接"
                System.out.println("没有客户端连接");
            }else{
                //获取与当前时间段（前3秒）有事件发生的Channel绑定的SelectionKey
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey s = iterator.next();
                    //判断事件是否为监听事件（与其对应的是客户端的Connect事件）
                    if (s.isAcceptable()) {
                        //获取监听到的套接字连接
                        SocketChannel client = ((ServerSocketChannel)s.channel()).accept();
                        //设置非阻塞
                        client.configureBlocking(false);
                        //注册，绑定的事件是读事件，还需在分配一个缓冲区给Channel
                        client.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                        System.out.println("客户端（" + client.hashCode() + "）连接");
                    }
                    //判断当前事件是否为读取事件（与其对应的是客户端的写入事件）
                    if (s.isReadable()) {
                        SocketChannel client = (SocketChannel) s.channel();
                        //将队列资源放入缓冲区
                        ByteBuffer content = (ByteBuffer) s.attachment();
                        client.read(content);
                        //切换读写模式
                        content.flip();
                        while (content.hasRemaining()) {
                            System.out.print((char) content.get());
                        }
                        //清空数据（不是真的清空，只是把position=0，limit=capacity）
                        content.clear();
                    }
                    iterator.remove();
                }
            }
        }
    }

}


