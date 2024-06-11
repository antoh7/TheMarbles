package com.themarbles.game;

import java.util.List;

/** class for testing **/
public class TestClass {
    public static void main(String[] args){
        method2();


    }

    private static void method2() {
        method1();
    }

    private static void method1() {
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        System.out.println("calling started");
        for (StackTraceElement element: traceElements) {
            System.out.println(element.toString());
        }
        System.out.println("calling finished");

    }
}
