package com.yzy.chain_resposible_mode;

public class ErrorHandler extends Handler{
    @Override
    public void handle(String request) {
        if (request.equals("fail")) {
            System.out.println("fail");
        } else {
            this.next.handle(request);
        }
    }
}
