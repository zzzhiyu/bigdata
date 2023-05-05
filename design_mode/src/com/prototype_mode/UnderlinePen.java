package com.prototype_mode;

import com.prototype_mode.framework.Product;
import java.nio.charset.StandardCharsets;

public class UnderlinePen extends Product{
    private final char ulchar;

    public UnderlinePen(char ulchar) {
        this.ulchar = ulchar;
    }

    @Override
    public void use(String s) {
        int length = s.getBytes(StandardCharsets.UTF_8).length;
        System.out.println("\"" + s + "\"");
        for (int i = 0; i < length; i++) {
            System.out.print(ulchar);
        }
        System.out.println();
    }
}
