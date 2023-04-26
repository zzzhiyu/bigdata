package com.factory_method_mode.homework.framework;

public abstract class Factory {
    public final Product create(String owner, long cardNo) {
        Product product = createProduct(owner, cardNo);
        registerProduct(product);
        return product;
    }

    protected abstract Product createProduct(String owner, long cardNo);

    protected abstract void registerProduct(Product product);
}
