package com.example.classloader.equality;

import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;

import com.example.classloader.OverriddenURLClassLoader;

/**
 * Created by duncan on 22/12/2015.
 */
public class ClassLoadersAreFun {
    public static void main(String[] args) throws Exception {

        Class<ClassLoadersAreFun> classLoadersAreFunClass = ClassLoadersAreFun.class;

        String myself = classLoadersAreFunClass.getName().replace('.', '/') + ".class";

        String myThingName = MyThing.class.getName();

        URL whereDoIComeFrom = goFindYourselfYoungGrasshopper(myself);

        // create ClassLoaders that deliberately load their own MyThing
        ClassLoader alpha = new OverriddenURLClassLoader(whereDoIComeFrom, myThingName, MyEnum.class.getName());
        ClassLoader beta = new OverriddenURLClassLoader(whereDoIComeFrom, myThingName, MyEnum.class.getName());

        final Class<MyThing> myThing = MyThing.class;
        final Class<? extends MyInterface> myThingAlpha = (Class<? extends MyInterface>)alpha.loadClass(myThingName);
        final Class<? extends MyInterface> myThingBeta = (Class<? extends MyInterface>)beta.loadClass(myThingName);
        System.out.println("\n*************************************\n\nLook Ma! Three classes: ");
        System.out.printf("%s from:%s @%x\n", myThing, myThing.getClassLoader(), System.identityHashCode(myThing));
        System.out.printf("%s from:%s @%x\n", myThingAlpha, myThingAlpha.getClassLoader(), System.identityHashCode(myThingAlpha));
        System.out.printf("%s from:%s @%x\n\n", myThingBeta, myThingBeta.getClassLoader(), System.identityHashCode(myThingBeta));

        final Class<?> myThingIf = myThing.getInterfaces()[0];
        final Class<?> myThingAlphaIf = myThingAlpha.getInterfaces()[0];
        final Class<?> myThingBetaIf = myThingBeta.getInterfaces()[0];
        System.out.println("But only one interface: ");
        System.out.printf("%s from:%s @%x\n", myThingIf, myThingIf.getClassLoader(), System.identityHashCode(myThingIf));
        System.out.printf("%s from:%s @%x\n", myThingAlphaIf, myThingAlphaIf.getClassLoader(), System.identityHashCode(myThingAlphaIf));
        System.out.printf("%s from:%s @%x\n\n", myThingBetaIf, myThingBetaIf.getClassLoader(), System.identityHashCode(myThingBetaIf));


        Constructor<? extends MyInterface> alphaConstructor = myThingAlpha.getConstructor(String.class);
        Constructor<? extends MyInterface> betaConstructor = myThingBeta.getConstructor(String.class);

        System.out.println("Let's create some things and use them: ");
        System.out.println("We can cast all the instances to MyInterface, as MyInterface is in the parent ClassLoader of all");
        String thingName = "I'm a MyThing";
        final MyInterface a = alphaConstructor.newInstance(thingName);
        final MyInterface a1 = alphaConstructor.newInstance(thingName);
        final MyInterface b = betaConstructor.newInstance(thingName);
        System.out.println("a:");
        System.out.println(a);
        System.out.println(a.tellMeSomething());
        System.out.println();
        System.out.println("a1:");
        System.out.println(a1);
        System.out.println(a1.tellMeSomething());
        System.out.println();
        System.out.println("b:");
        System.out.println(b);
        System.out.println(b.tellMeSomething());
        System.out.println();
        System.out.println("But wait, that's a static!?");
        System.out.println("Yes, but a static member of the Class, of which we have 3");
        System.out.println();

        Test.test(() -> myThingAlpha == myThingBeta, "Are Class Objects == ? ");

        Test.test(() -> myThingAlpha.equals(myThingBeta), "Are Class Objects equals()? ");

        Test.test(() -> a == a, "a == a ?       => ");

        Test.test(() -> a.equals(a), "a.equals(a) ?  => ");

        Test.test(() -> a == a1, "a == a1 ?      => ");

        Test.test(() -> a.equals(a1), "a.equals(a1) ? => ");

        Test.test(() -> a == b, "a == b ?       => ");

        Test.test(() -> a.equals(b), "a.equals(b) ?  => ");

        Test.test(() -> new MyThing("And another thing..."), "Can I instantiate one?  => ");

        Test.test(() -> MyThing.class.isAssignableFrom(myThingAlpha), "Is myThingAlpha assignable to MyThing? => ");

        Test.test(() -> a instanceof MyThing, "Is 'a' an instance of MyThing? => ");

        Test.test(() -> {
                 MyThing theThing = (MyThing)a;
                 return theThing.tellMeSomething();
             }, "Can I cast 'a' to MyThing?     => ");

        System.out.println();
        System.out.println("That's a fun thing to see in your app server logs :)\n\nAnd as for enums:");

        // and now for some enums:
        Class<? extends Enum<?>> alphaEnum = (Class<? extends Enum<?>>)alpha.loadClass(MyEnum.class.getName());
        // good, it's an Enum.  grab the first constant value
        final Enum<?> anEnum = alphaEnum.getEnumConstants()[0];
        final MyEnum instance1 = MyEnum.INSTANCE_1;

        Test.test(() -> instance1.equals(anEnum),
                  "%s.%s.equals(%s.%s) ? => ", instance1.getClass().getSimpleName(), instance1, anEnum.getClass().getSimpleName(), anEnum);
    }


    protected static URL goFindYourselfYoungGrasshopper(String myself) throws MalformedURLException {
        URL clRfun = Thread.currentThread().getContextClassLoader().getResource(myself);
        String s = clRfun.toString();
        System.out.print("Loaded '");
        System.out.print(myself);
        System.out.print("' from :");
        System.out.println(s);
        int jarPos = s.lastIndexOf(".jar!");

        String substring;
        if (jarPos >= 0) {

            substring = s.substring(s.startsWith("jar:") ? 4: 0, jarPos + 4);
            System.out.println(substring);
        } else {
            jarPos = s.indexOf(myself);
            if (jarPos < 0) {
                jarPos = s.indexOf(myself.replace('/', '\\'));
            }
            substring = s.substring(0, jarPos);
        }
        System.out.print("Using '");
        System.out.print(substring);
        System.out.println("' as ClassLoader source.");
        return new URL(substring);
    }

}
