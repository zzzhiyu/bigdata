package com.yzy.chain_resposible_mode;

public class Demo {
    public static void main(String[] args) {
        ConcreteHandler concreteHandler = new ConcreteHandler();
        ErrorHandler errorHandler = new ErrorHandler();
        EndHandler endHandler = new EndHandler();
        concreteHandler.next = errorHandler;
        errorHandler.next = endHandler;
        endHandler.next = concreteHandler;

        concreteHandler.handle("success");
        concreteHandler.handle("fail");
        concreteHandler.handle("ddddd");
    }
}
