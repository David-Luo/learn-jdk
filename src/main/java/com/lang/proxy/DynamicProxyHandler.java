package com.lang.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DynamicProxyHandler  implements InvocationHandler {
    private Object realObject;

    public DynamicProxyHandler(Object realObject) {
        this.realObject = realObject;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //代理扩展逻辑
        System.out.println("proxy do");

        return method.invoke(realObject, args);
    }
    public static void main(String[] args) {
        System.getProperties().setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        RealObject realObject = new RealObject();
        Action proxy = (Action) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(), new Class[]{Action.class}, new DynamicProxyHandler(realObject));
        proxy.doSomething();
    }
}