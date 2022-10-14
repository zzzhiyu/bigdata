package com.yzy.chain_resposible_mode;

public class EndHandler extends Handler{
    @Override
    public void handle(String request) {
        if (!request.equals("success") && !request.equals("fail")) {
            System.out.println("can not handle");
        } else {
            this.next.handle(request);
        }
    }
}
