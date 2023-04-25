package com.adapter_mode.homework;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        FileIO f = new FileProperties();
        try {
            f.readFormFile("D:\\softeware\\git_space\\bigdata\\design_mode\\src\\com\\adapter_model\\homework\\file.txt");
            f.setValue("year", "2004");
            f.setValue("month", "4");
            f.setValue("day", "21");
            f.writeToFile("D:\\softeware\\git_space\\bigdata\\design_mode\\src\\com\\adapter_model\\homework\\newfile.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
