package com.reggie.common;

/**
 * 用于保存用户id，基于ThreadLocal
 * 原理在于每次请求都会开辟一个线程
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

}
