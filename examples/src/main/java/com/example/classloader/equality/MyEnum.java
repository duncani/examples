package com.example.classloader.equality;

/**
 * Created by duncan on 22/12/2015.
 */
public enum MyEnum implements MyInterface {
    INSTANCE_1,
    INSTANCE_2;

    @Override
    public String tellMeSomething() {
        return "I am :" + this.toString() + " @" + System.identityHashCode(this);
    }
}
