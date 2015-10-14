package com.goodcodeforfun.cleancitybattery.event;

/**
 * Created by snigavig on 27.09.15.
 */
abstract class AbstractEvent {
    private final Enum _type;

    AbstractEvent(Enum type) {
        this._type = type;
    }

    Enum getType() {
        return this._type;
    }
}
