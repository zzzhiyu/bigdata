package com.visitor_mode;

public interface Element {
    public abstract void accept(Visitor v);
}
