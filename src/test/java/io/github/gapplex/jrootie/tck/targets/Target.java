package io.github.gapplex.jrootie.tck.targets;

public class Target {
    private static final int num = 1;

    private final String s;

    public Target(String s){
        this.s = s;
    }

    public int compute(){
        return 1;
    }

    public static int getNum(){
        return num;
    }

    public String getS(){
        return s;
    }

    public void voidMethod(){}

    public static String render(Object o){
        return o == null ? "null" : o.toString();
    }
}
