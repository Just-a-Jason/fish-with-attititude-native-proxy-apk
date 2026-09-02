package org.cocos2dx.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.DisplayMetrics;
import android.util.Log;
import java.util.Locale;

public class Cocos2dxActivity extends Activity {
  private static final int HANDLER_SHOW_DIALOG = 1;
  
  private static Cocos2dxAccelerometer accelerometer;
  
  private static boolean accelerometerEnabled = false;
  
  private static Cocos2dxMusic backgroundMusicPlayer;
  
  private static Handler handler;
  
  private static String packageName;
  
  private static Cocos2dxSound soundPlayer;
  
  public static void disableAccelerometer() {
    accelerometerEnabled = false;
    accelerometer.disable();
  }
  
  public static void enableAccelerometer() {
    accelerometerEnabled = true;
    accelerometer.enable();
  }
  
  public static void end() {
    backgroundMusicPlayer.end();
    soundPlayer.end();
  }
  
  public static float getBackgroundMusicVolume() {
    return backgroundMusicPlayer.getBackgroundVolume();
  }
  
  public static String getCocos2dxPackageName() {
    return packageName;
  }
  
  public static String getCurrentLanguage() {
    return Locale.getDefault().getLanguage();
  }
  
  public static float getEffectsVolume() {
    return soundPlayer.getEffectsVolume();
  }
  
  public static boolean isBackgroundMusicPlaying() {
    return backgroundMusicPlayer.isBackgroundMusicPlaying();
  }
  
  private static native void nativeSetPaths(String paramString);
  
  public static void pauseAllEffects() {
    soundPlayer.pauseAllEffects();
  }
  
  public static void pauseBackgroundMusic() {
    backgroundMusicPlayer.pauseBackgroundMusic();
  }
  
  public static void pauseEffect(int paramInt) {
    soundPlayer.pauseEffect(paramInt);
  }
  
  public static void playBackgroundMusic(String paramString, boolean paramBoolean) {
    backgroundMusicPlayer.playBackgroundMusic(paramString, paramBoolean);
  }
  
  public static int playEffect(String paramString, boolean paramBoolean) {
    return soundPlayer.playEffect(paramString, paramBoolean);
  }
  
  public static void preloadBackgroundMusic(String paramString) {
    backgroundMusicPlayer.preloadBackgroundMusic(paramString);
  }
  
  public static void preloadEffect(String paramString) {
    soundPlayer.preloadEffect(paramString);
  }
  
  public static void resumeAllEffects() {
    soundPlayer.resumeAllEffects();
  }
  
  public static void resumeBackgroundMusic() {
    backgroundMusicPlayer.resumeBackgroundMusic();
  }
  
  public static void resumeEffect(int paramInt) {
    soundPlayer.resumeEffect(paramInt);
  }
  
  public static void rewindBackgroundMusic() {
    backgroundMusicPlayer.rewindBackgroundMusic();
  }
  
  public static void setBackgroundMusicVolume(float paramFloat) {
    backgroundMusicPlayer.setBackgroundVolume(paramFloat);
  }
  
  public static void setEffectsVolume(float paramFloat) {
    soundPlayer.setEffectsVolume(paramFloat);
  }
  
  private void showDialog(String title, String msg) {
    (new AlertDialog.Builder((Context)this)).setTitle(title).setMessage(msg).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface param1DialogInterface, int param1Int) {}
        }).create().show();
  }
  
  public static void showMessageBox(String title, String msg) {
    Message message = new Message();
    message.what = 1;
    message.obj = new DialogMessage(title, msg);
    handler.sendMessage(message);
  }
  
  public static void stopAllEffects() {
    soundPlayer.stopAllEffects();
  }
  
  public static void stopBackgroundMusic() {
    backgroundMusicPlayer.stopBackgroundMusic();
  }
  
  public static void stopEffect(int paramInt) {
    soundPlayer.stopEffect(paramInt);
  }
  
  public static void terminateProcess() {
    Process.killProcess(Process.myPid());
  }
  
  public static void unloadEffect(String paramString) {
    soundPlayer.unloadEffect(paramString);
  }
  
  protected void onCreate(Bundle paramBundle) {
    super.onCreate(paramBundle);
    DisplayMetrics displayMetrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    accelerometer = new Cocos2dxAccelerometer((Context)this);
    backgroundMusicPlayer = new Cocos2dxMusic((Context)this);
    soundPlayer = new Cocos2dxSound((Context)this);
    Cocos2dxBitmap.setContext((Context)this);
    handler = new Handler() {
        public void handleMessage(Message param1Message) {
          switch (param1Message.what) {
            default:
              return;
            case 1:
              break;
          } 
          Cocos2dxActivity.this.showDialog(((DialogMessage)param1Message.obj).title, ((DialogMessage)param1Message.obj).message);
        }
      };
  }
  
  protected void onPause() {
    super.onPause();
    if (accelerometerEnabled)
      accelerometer.disable(); 
  }
  
  protected void onResume() {
    super.onResume();
    if (accelerometerEnabled)
      accelerometer.enable(); 
  }
  
  protected void setPackageName(String paramString) {
    packageName = paramString;
    PackageManager packageManager = getApplication().getPackageManager();
    try {
      ApplicationInfo applicationInfo = packageManager.getApplicationInfo(paramString, 0);
      String str = applicationInfo.sourceDir;
      Log.w("apk path", str);
      nativeSetPaths(str);
      return;
    } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
      nameNotFoundException.printStackTrace();
      throw new RuntimeException("Unable to locate assets, aborting...");
    } 
  }
}


/* Location:              /home/jason/Pobrane/fish.jar!/org/cocos2dx/lib/Cocos2dxActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */