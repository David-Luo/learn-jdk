package com.lang.proxy;

public class StaticProxy implements Action {
    private Action realObject;

    public StaticProxy(Action realObject) {
        this.realObject = realObject;
    }
    public void doSomething() {
        System.out.println("proxy do");
        realObject.doSomething();
    }

    public static void main(String[] args) {
        StaticProxy staticProxy = new StaticProxy(new RealObject());
        staticProxy.doSomething();
    }
}