package com.factory_method_mode.homework.idcard;

import com.factory_method_mode.homework.framework.Factory;
import com.factory_method_mode.homework.framework.Product;

import java.util.HashMap;
import java.util.Map;

public class IDCardFactory extends Factory {
    private final Map<String, Long> cardInfo = new HashMap<>();

    @Override
    protected Product createProduct(String owner, long cardNo) {
        return new IDCard(owner, cardNo);
    }

    @Override
    protected void registerProduct(Product product) {
        IDCard idCard = (IDCard)product;
        cardInfo.put(idCard.getOwner(), idCard.getCardNo());
    }

    public Map<String, Long> getCardInfo() {
        return cardInfo;
    }
}
