package com.adapter_mode.homework;

import java.io.IOException;

public interface FileIO {
    void readFormFile(String filename) throws IOException;
    void writeToFile(String filename) throws IOException;
    void setValue(String key, String value);
    String getValue(String key);
}
