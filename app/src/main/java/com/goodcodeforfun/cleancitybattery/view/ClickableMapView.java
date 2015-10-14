package com.goodcodeforfun.cleancitybattery.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

/**
 * Created by snigavig on 27.09.15.
 */
public class ClickableMapView extends MapView {
    private static final String EXCEPTION_DESCRIPTION = " must implement UpdateMapAfterUserInterection";
    private UpdateMapAfterUserInterection updateMapAfterUserInterection;

    public ClickableMapView(Context context) {
        super(context);
        try {
            updateMapAfterUserInterection = (UpdateMapAfterUserInterection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + EXCEPTION_DESCRIPTION);
        }
    }

    public ClickableMapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        try {
            updateMapAfterUserInterection = (UpdateMapAfterUserInterection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + EXCEPTION_DESCRIPTION);
        }
    }

    public ClickableMapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        try {
            updateMapAfterUserInterection = (UpdateMapAfterUserInterection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + EXCEPTION_DESCRIPTION);
        }
    }

    public ClickableMapView(Context context, GoogleMapOptions options) {
        super(context, options);
        try {
            updateMapAfterUserInterection = (UpdateMapAfterUserInterection) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + EXCEPTION_DESCRIPTION);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        updateMapAfterUserInterection.onUpdateMapAfterUserInterection();
        return super.dispatchTouchEvent(ev);
    }

    // Map Activity must implement this interface
    public interface UpdateMapAfterUserInterection {
        void onUpdateMapAfterUserInterection();
    }
}