package ru.nsu.ccfit.zharkova.Factory.Detail;

import ru.nsu.ccfit.zharkova.Factory.System.IDManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yana on 25.04.15.
 */
public class Detail {

    private String ID;
    private final DetailType type;

    public Detail(DetailType type){
        this.type = type;
    }

    public String getID(){
        return ID;
    }

    public void setID(int ID){
        this.ID = type.getDetailName() + "<" + ID + ">";
    }
}