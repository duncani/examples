package com.example.classloader.equality;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by duncan on 22/12/2015.
 */
public final class MyThing implements MyInterface {
    private final String name;

    public MyThing(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MyThing{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MyThing myThing = (MyThing)o;

        return name != null ? name.equals(myThing.name) : myThing.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String tellMeSomething() {
        return "This is invocation: " + counter.incrementAndGet();
    }

    public static final AtomicLong counter = new AtomicLong();
}
