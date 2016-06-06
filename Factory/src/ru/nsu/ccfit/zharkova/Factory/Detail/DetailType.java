package ru.nsu.ccfit.zharkova.Factory.Detail;

/**
 * Created by Yana on 25.04.15.
 */
public enum DetailType {

    ACCESSORY("ACCESSORY: "),
    BODY("BODY: "),
    MOTOR("MOTOR: "),
    CAR("CAR ");

    private String type;

    DetailType(String type) {
        this.type = type;
    }

    public String getDetailName() {
        return type;
    }
}
