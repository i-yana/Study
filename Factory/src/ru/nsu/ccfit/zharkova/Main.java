package ru.nsu.ccfit.zharkova;

import ru.nsu.ccfit.zharkova.Factory.Factory;

/**
 * Created by Yana on 25.04.15.
 */
public class Main {

    public static void main(String[] args) {

        Factory factory1 = new Factory("Toyota", "conf1.txt");
        Factory factory2 = new Factory("BMW", "conf2.txt");
        factory1.start();
        factory2.start();
    }
}