package com.factory_method_mode.homework;
import com.factory_method_mode.homework.framework.Factory;
import com.factory_method_mode.homework.framework.Product;
import com.factory_method_mode.homework.idcard.IDCardFactory;

public class Main {
    public static void main(String[] args) {
        IDCardFactory factory = new IDCardFactory();
        Product card1 = factory.create("小明", 1);
        Product card2 = factory.create("小红", 2);
        Product card3 = factory.create("小钢", 3);
        card1.use();
        card2.use();
        card3.use();
        System.out.println(factory.getCardInfo());
    }
}
