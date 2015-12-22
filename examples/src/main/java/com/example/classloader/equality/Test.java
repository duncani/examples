package com.example.classloader.equality;

/**
 * Created by duncan on 22/12/2015.
 */
public class Test {

    public static void test(TestFunction test, String message, Object... args) {
        System.out.printf(message, args);
        try {
            System.out.println(test.call());
        }
        catch (Exception t) {
            System.out.print("Threw ");
            System.out.print(t.getClass().getName());
            System.out.print(' ');
            System.out.println(t.getMessage());
        }
    }

    public interface TestFunction {
        Object call();
    }
}
