package com.adapter_mode.homework;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class FileProperties extends Properties implements FileIO{

    public FileProperties() {}

    @Override
    public void readFormFile(String filename) throws IOException {
        super.load(new FileInputStream(filename));
    }

    @Override
    public void writeToFile(String filename) throws IOException {
        super.store(new FileOutputStream(filename), "");
    }

    @Override
    public void setValue(String key, String value) {
        super.setProperty(key, value);
    }

    @Override
    public String getValue(String key) {
        return super.getProperty(key);
    }
}
