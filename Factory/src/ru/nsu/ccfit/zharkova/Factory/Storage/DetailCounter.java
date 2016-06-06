package ru.nsu.ccfit.zharkova.Factory.Storage;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Yana on 25.04.15.
 */
public class DetailCounter {

    private final AtomicLong counter = new AtomicLong(0);

    public void increment(){
        counter.incrementAndGet();
    }

    public AtomicLong getCount(){
        return counter;
    }
}
