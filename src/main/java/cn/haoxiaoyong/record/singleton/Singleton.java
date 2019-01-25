package cn.haoxiaoyong.record.singleton;

/**
 * Created by haoxy on 2019/1/25.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class Singleton {


    public static void main(String[] args) {
        /*Singleton_hungry sh = Singleton_hungry.sh;
         Singleton_hungry.sh=null;
        Singleton_hungry sh2 = Singleton_hungry.sh;
        System.out.println(sh == sh2);*/
        //饿汉式
        /*Singleton_hungry getinit = Singleton_hungry.getinit();
        Singleton_hungry getinit1 = Singleton_hungry.getinit();
        System.out.println(getinit == getinit1);*/
        //懒汉式
       /* Singleton_lazy singleton_lazy = Singleton_lazy.getSingleton_lazy();
        Singleton_lazy singleton_lazy1 = Singleton_lazy.getSingleton_lazy();
        System.out.println(singleton_lazy == singleton_lazy1);*/
       //第三种方式
        Singleton_final singleton_final = Singleton_final.singleton_final;
        Singleton_final singleton_final1 = Singleton_final.singleton_final;
        System.out.println(singleton_final==singleton_final1);
    }


}


class Singleton_hungry {

    //私有构造方法
    private Singleton_hungry() {

    }

    //创建本类对象
    private static Singleton_hungry sh = new Singleton_hungry();

    //对外提供公共的访问方法
    public static Singleton_hungry getinit() {
        return sh;
    }

}

class Singleton_lazy {
    //私有构造方法
    private Singleton_lazy() {
    }

    //创建本类对象
    private static Singleton_lazy singleton_lazy;

    public static Singleton_lazy getSingleton_lazy() {
        if (singleton_lazy == null) {
            singleton_lazy = new Singleton_lazy();
        }
        return singleton_lazy;
    }

}

class Singleton_final {

    private Singleton_final() {
    }

    public static final Singleton_final singleton_final = new Singleton_final();

}
