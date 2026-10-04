package io.github.gapplex.jrootie.tck.targets;

public class MockString {
    public static String valueOf(Object o){
        return o == null ? "null" : o.toString();
    }
}
