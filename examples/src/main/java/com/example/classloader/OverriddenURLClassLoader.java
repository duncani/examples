package com.example.classloader;

import static java.util.Arrays.asList;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;

/**
 * This mimics the common WAR ClassLoader pattern of loading WEB-INF/classes & WEB-INF/lib before parent ClassLoaders,
 * but without having to jump through hoops about where classes live etc.
 */
public class OverriddenURLClassLoader extends URLClassLoader {
    private final HashSet<String> overrideNames;

    public OverriddenURLClassLoader(URL loadFrom, String... overriddenNames) {
        this(Thread.currentThread().getContextClassLoader(), loadFrom, overriddenNames);
    }
    public OverriddenURLClassLoader(ClassLoader contextClassLoader, URL loadFrom, String... overrideNames) {
        super(new URL[]{
                loadFrom
        }, contextClassLoader);
        this.overrideNames = new HashSet<>(asList(overrideNames));
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {

        synchronized (this.getClassLoadingLock(name)) {
            // First, check if the class has already been loaded
            Class c = this.findLoadedClass(name);
            if (c == null) {
                // if it's not one of our overrides, delegate to standard URLClassLoader impl
                if (!overrideNames.contains(name)) {
                    return super.loadClass(name, resolve);
                }
                // go get that sucker ourselves
                c = this.findClass(name);
            }
            if (resolve) {
                this.resolveClass(c);
            }
            return c;
        }
    }
}
