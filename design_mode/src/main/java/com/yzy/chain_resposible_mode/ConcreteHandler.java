package com.yzy.chain_resposible_mode;

public class ConcreteHandler extends Handler{
    @Override
    public void handle(String request) {
        if ("success".equals(request)) {
            System.out.println("ConcreteHandlerA 处理成功");
        } else {
            this.next.handle(request);
        }
    }
}
