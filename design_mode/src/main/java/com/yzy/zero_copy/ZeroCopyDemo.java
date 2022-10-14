package com.yzy.zero_copy;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class ZeroCopyDemo {
    public static void main(String[] args) {
        try {
            FileChannel readChanel = FileChannel.open(
                    Paths.get("D:\\softeware\\IDEA\\warehouse\\desi\\src\\main\\resources\\test.txt"),
                    StandardOpenOption.READ);
            long len = readChanel.size();
            long position = readChanel.position();
            FileChannel writeChannel = FileChannel.open(
                    Paths.get("D:\\softeware\\IDEA\\warehouse\\desi\\src\\main\\resources\\test02.txt"),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE);
            readChanel.transferTo(position, len, writeChannel);
            readChanel.close();
            writeChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
