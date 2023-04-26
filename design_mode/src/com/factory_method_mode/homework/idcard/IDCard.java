package com.factory_method_mode.homework.idcard;

import com.factory_method_mode.homework.framework.Product;

public class IDCard extends Product {
    private final String owner;
    private final long cardNo;

    IDCard(String owner, long cardNo) {
        this.owner = owner;
        this.cardNo = cardNo;
    }

    @Override
    public void use() {
        System.out.println(owner + "使用卡号: " + cardNo + " 一次!");
    }

    public String getOwner() {
        return owner;
    }

    public long getCardNo() {
        return cardNo;
    }
}
