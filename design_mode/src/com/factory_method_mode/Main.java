package com.factory_method_mode;

import com.factory_method_mode.framework.Factory;
import com.factory_method_mode.framework.Product;
import com.factory_method_mode.idcard.IDCardFactory;

public class Main {
    public static void main(String[] args) {
        Factory factory = new IDCardFactory();
        Product card1 = factory.create("小明");
        Product card2 = factory.create("小红");
        Product card3 = factory.create("小钢");
        card1.use();
        card2.use();
        card3.use();
    }
}
