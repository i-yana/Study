package ru.nsu.ccfit.zharkova.Factory.Detail;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yana on 25.04.15.
 */
public class Car extends Detail {

    private Accessory accessory;
    private Body body;
    private Motor motor;

    public Car(Accessory accessory, Body body, Motor motor){
        super(DetailType.CAR);
        this.accessory = accessory;
        this.body = body;
        this.motor = motor;
    }

    public String getFullCarID(){
        return new String(getID() + "(" + body.getID() + "," + motor.getID() + "," + accessory.getID() + ")");
    }

}