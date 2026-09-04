package org.cocos2dx.lib;

import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Cocos2dxRenderer implements GLSurfaceView.Renderer {

    private static final long NANOSECONDSPERMINISECOND = 1000000L;

    private static final long NANOSECONDSPERSECOND = 1000000000L;

    private static long animationInterval = 16666666L;

    private long last;

    private int screenHeight;

    private int screenWidth;

    private static native void nativeDeleteBackward();

    private static native String nativeGetContentText();

    private static native void nativeInit(int paramInt1, int paramInt2);

    private static native void nativeInsertText(String paramString);

    private static native boolean nativeKeyDown(int paramInt);

    private static native void nativeOnPause();

    private static native void nativeOnResume();

    private static native void nativeRender();

    private static native void nativeTouchesBegin(
        int paramInt,
        float paramFloat1,
        float paramFloat2,
        long paramLong
    );

    private static native void nativeTouchesCancel(
        int[] paramArrayOfint,
        float[] paramArrayOffloat1,
        float[] paramArrayOffloat2,
        long paramLong
    );

    private static native void nativeTouchesEnd(
        int paramInt,
        float paramFloat1,
        float paramFloat2,
        long paramLong
    );

    private static native void nativeTouchesMove(
        int[] paramArrayOfint,
        float[] paramArrayOffloat1,
        float[] paramArrayOffloat2,
        long paramLong
    );

    public static void setAnimationInterval(double paramDouble) {
        animationInterval = (long) (1.0E9D * paramDouble);
    }

    public String getContentText() {
        return nativeGetContentText();
    }

    public void handleActionCancel(
        int[] paramArrayOfint,
        float[] paramArrayOffloat1,
        float[] paramArrayOffloat2,
        long paramLong
    ) {
        nativeTouchesCancel(
            paramArrayOfint,
            paramArrayOffloat1,
            paramArrayOffloat2,
            paramLong
        );
    }

    public void handleActionDown(
        int paramInt,
        float paramFloat1,
        float paramFloat2,
        long paramLong
    ) {
        nativeTouchesBegin(paramInt, paramFloat1, paramFloat2, paramLong);
    }

    public void handleActionMove(
        int[] paramArrayOfint,
        float[] paramArrayOffloat1,
        float[] paramArrayOffloat2,
        long paramLong
    ) {
        nativeTouchesMove(
            paramArrayOfint,
            paramArrayOffloat1,
            paramArrayOffloat2,
            paramLong
        );
    }

    public void handleActionUp(
        int paramInt,
        float paramFloat1,
        float paramFloat2,
        long paramLong
    ) {
        nativeTouchesEnd(paramInt, paramFloat1, paramFloat2, paramLong);
    }

    public void handleDeleteBackward() {
        nativeDeleteBackward();
    }

    public void handleInsertText(String paramString) {
        nativeInsertText(paramString);
    }

    public void handleKeyDown(int paramInt) {
        nativeKeyDown(paramInt);
    }

    public void handleOnPause() {
        nativeOnPause();
    }

    public void handleOnResume() {
        nativeOnResume();
    }

    public void onDrawFrame(GL10 paramGL10) {
        long l1 = System.nanoTime();
        long l2 = l1 - this.last;
        nativeRender();
        if (l2 < animationInterval) try {
            Thread.sleep((animationInterval - l2) / 1000000L);
        } catch (Exception exception) {}
        this.last = l1;
    }

    public void onSurfaceChanged(
        GL10 paramGL10,
        int paramInt1,
        int paramInt2
    ) {}

    public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
        nativeInit(this.screenWidth, this.screenHeight);
        this.last = System.nanoTime();
    }

    public void setScreenWidthAndHeight(int paramInt1, int paramInt2) {
        this.screenWidth = paramInt1;
        this.screenHeight = paramInt2;
    }
}
