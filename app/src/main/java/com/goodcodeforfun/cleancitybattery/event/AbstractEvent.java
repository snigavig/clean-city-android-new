package com.goodcodeforfun.cleancitybattery.event;

/**
 * Created by snigavig on 27.09.15.
 */
public abstract class AbstractEvent {
    private Enum _type;

    protected AbstractEvent(Enum type) {
        this._type = type;
    }

    public Enum getType() {
        return this._type;
    }
}
