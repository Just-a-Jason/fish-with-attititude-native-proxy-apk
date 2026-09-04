package org.cocos2dx.lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;

public class Cocos2dxAccelerometer implements SensorEventListener {

    private static final String TAG = "Cocos2dxAccelerometer";

    private Sensor mAccelerometer;

    private Context mContext;

    private int mNaturalOrientation;

    private SensorManager mSensorManager;

    public Cocos2dxAccelerometer(Context paramContext) {
        this.mContext = paramContext;
        this.mSensorManager = (SensorManager) this.mContext.getSystemService(
            "sensor"
        );
        this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
        this.mNaturalOrientation = (
            (WindowManager) this.mContext.getSystemService("window")
        )
            .getDefaultDisplay()
            .getOrientation();
    }

    private static native void onSensorChanged(
        float paramFloat1,
        float paramFloat2,
        float paramFloat3,
        long paramLong
    );

    public void disable() {
        this.mSensorManager.unregisterListener(this);
    }

    public void enable() {
        this.mSensorManager.registerListener(this, this.mAccelerometer, 1);
    }

    public void onAccuracyChanged(Sensor paramSensor, int paramInt) {}

    public void onSensorChanged(SensorEvent paramSensorEvent) {
        if (paramSensorEvent.sensor.getType() == 1) {
            float f1;
            float f2;
            float f3 = paramSensorEvent.values[0];
            float f4 = paramSensorEvent.values[1];
            float f5 = paramSensorEvent.values[2];
            int i = this.mContext.getResources().getConfiguration().orientation;
            if (i == 2 && this.mNaturalOrientation != 0) {
                f2 = -f4;
                f1 = f3;
            } else {
                f2 = f3;
                f1 = f4;
                if (i == 1) {
                    f2 = f3;
                    f1 = f4;
                    if (this.mNaturalOrientation != 0) {
                        f2 = f4;
                        f1 = -f3;
                    }
                }
            }
            onSensorChanged(f2, f1, f5, paramSensorEvent.timestamp);
        }
    }
}
