package com.adapter_model.homework;

import java.io.IOException;

public interface FileIO {
    void readFormFile(String filename) throws IOException;
    void writeToFile(String filename) throws IOException;
    void setValue(String key, String value);
    String getValue(String key);
}
