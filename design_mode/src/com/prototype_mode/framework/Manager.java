package com.prototype_mode.framework;
import java.util.HashMap;

public class Manager {
    private final HashMap<String, Object> showcase = new HashMap<>();

    public void register(String name, Product product) {
        showcase.put(name, product);
    }

    public Product create(String proName) {
        Product product = (Product) showcase.get(proName);
        return product.createClone();
    }
}
