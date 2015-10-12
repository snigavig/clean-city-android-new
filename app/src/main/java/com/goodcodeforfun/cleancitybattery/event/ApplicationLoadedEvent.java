package com.goodcodeforfun.cleancitybattery.event;

/**
 * Created by snigavig on 27.09.15.
 */
public class ApplicationLoadedEvent extends AbstractEvent {
    public static final int OK_RESULT_CODE = 0;
    public static final int FAIL_RESULT_CODE = 1;
    public static final int NO_CODE = -1;
    private int _resultCode;

    public ApplicationLoadedEvent(ApplicationLoadedEvent event) {
        super(event.getType());
        this._resultCode = event.getResultCode();
    }

    public ApplicationLoadedEvent(ApplicationLoadedType type, int resultCode) {
        super(type);
        this._resultCode = resultCode;
    }

    @Override
    public Enum getType() {
        return super.getType();
    }

    public int getResultCode() {
        return _resultCode;
    }

    public enum ApplicationLoadedType {
        COMPLETED,
        STARTED
    }
}