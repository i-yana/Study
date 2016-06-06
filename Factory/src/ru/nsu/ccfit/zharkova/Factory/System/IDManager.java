package ru.nsu.ccfit.zharkova.Factory.System;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Yana on 25.04.15.
 */
public class IDManager {

    private AtomicInteger productID;
    private AtomicInteger threadID;

    public IDManager(){
        productID = new AtomicInteger(0);
        threadID = new AtomicInteger(0);
    }

    public int getProductId() {
        return productID.incrementAndGet();
    }

    public int getThreadID() {
        return threadID.incrementAndGet();
    }
}

