package com.helloworld;

public class HelloWorld {
    static {
        System.loadLibrary("rusty_fish"); 
    }

    public static native String hello();
}
