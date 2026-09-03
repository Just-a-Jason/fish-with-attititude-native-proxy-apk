package com.crowdstar.aquarium;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
// Toast
import android.widget.Toast;
import com.crowdstar.aquarium.util.LocalNotifications;
// Resources class
import com.crowdstar.hamobile.google.R;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import org.cocos2dx.lib.Cocos2dxActivity;
import org.cocos2dx.lib.Cocos2dxEditText;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

// Custom classes
// import com.just_a_jason.welcomer.Welcomer;

public class Aquarium extends Cocos2dxActivity {

    private static final String AD_BREED = "Breed";

    private static final String AD_DIG = "Dig";

    public static final int DIALOG_NEW_VERSION = 2;

    public static final int DIALOG_NEW_VERSION_FORCE = 1;

    private static final String FLURRY_COOKIE_KEY_CSUDID = "csudid";

    public static final String IAP_VALID = "25163452";

    public static final String INSTALL = "INSTALL";

    public static Aquarium INSTANCE;

    public static final String MAT_ACTION_START = "start";

    public static final String MAT_ACTION_STOP = "stop";

    public static final int PERIOD = 10000;

    public static final String REINSTALL = "REINSTALL";

    private static final String TAG = "FWA_Aquarium";

    public static final String UPDATE = "UPDATE";

    public static final float VOLUME_STEP = 0.14285715F;

    private static boolean usingAmazonIap = false;

    private boolean backgroundMusicPlaying = true;

    private Map<String, String> flurryCookies;

    private Handler handler;

    private boolean isConnected = true;

    private KeyguardManager keyguardManager;

    private Cocos2dxGLSurfaceView mGLView;

    private boolean paused = false;

    private boolean pleaseWaitViewShowing = false;

    private PowerManager pm;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            Aquarium.this.resumeMusic();
        }
    };

    private boolean refreshMusicState = true;

    private boolean registered = false;

    private boolean scheduleNativeAdjustSync = true;

    private final BroadcastReceiver screenReceiver = new BroadcastReceiver() {
        public void onReceive(Context param1Context, Intent param1Intent) {
            String str = param1Intent.getAction();
            if (str.equals("android.intent.action.SCREEN_ON")) {
                Aquarium.logd("Screen On");
                Aquarium.access$302(Aquarium.this, true);
                return;
            }
            if (str.equals("android.intent.action.SCREEN_OFF")) {
                Aquarium.logd("Screen Off");
                Aquarium.this.nativeResync();
            }
        }
    };

    private Timer timer;

    // Load game .so file
    static {
        System.loadLibrary("game");
    }

    private void clearDirectory(File paramFile) {
        if (paramFile.isDirectory()) {
            logd("Clearing directory " + paramFile.getAbsolutePath());
            File[] arrayOfFile = paramFile.listFiles();
            int i = arrayOfFile.length;
            byte b = 0;
            while (true) {
                if (b < i) {
                    File file = arrayOfFile[b];
                    if (file.isDirectory()) {
                        clearDirectory(file);
                    } else if (
                        !file
                            .getName()
                            .equals(
                                "da39a3ee5e6b4b0d3255bfef95601890afd80709.json"
                            )
                    ) {
                        logd("Deleting file " + file.getAbsolutePath());
                        file.delete();
                    }
                    b++;
                    continue;
                }
                return;
            }
        }
    }

    private void clearDownloaded() {
        // Odkomentowany i uproszczony kod z poprzedniej wersji
        /*
    SharedPreferences sharedPreferences = getPreferences(0);
    try {
      int i = (getPackageManager().getPackageInfo((getApplicationInfo()).packageName, 0)).versionCode;
      if (sharedPreferences.getInt("clear_downloads", 0) < i) {
        logd(getFilesDir().getAbsolutePath());
        File file = getFilesDir().getParentFile();
        String[] arrayOfString = new String[9];

        arrayOfString[0] = "fish";
        arrayOfString[1] = "props";
        arrayOfString[2] = "Sand";
        arrayOfString[3] = "Backgrounds";
        arrayOfString[4] = "decor";
        arrayOfString[5] = "Chests";
        arrayOfString[6] = "Sounds";
        arrayOfString[7] = "event";
        arrayOfString[8] = "facebook";

        int j = arrayOfString.length;

        for (byte b = 0; b < j; b++) {
          String str = arrayOfString[b];
          File file1 = new File(file, str);
          clearDirectory(file1);
        }
      }
      sharedPreferences.edit().putInt("clear_downloads", i).commit();
    } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
      logd(nameNotFoundException.getMessage());
    }
    */
    }

    private boolean detectOpenGLES20() {
        return (
            ((ActivityManager) getSystemService("activity"))
                .getDeviceConfigurationInfo()
                .reqGlEsVersion >= 131072
        );
    }

    public static native void dialogCompleteWithUrl(String paramString);

    public static native void dialogDidNotCompleteWithUrl(String paramString);

    public static native void didFailWithError(String paramString);

    private int getRealWidth() {
        return getWindowManager().getDefaultDisplay().getWidth();
    }

    private void hideKeyboard() {
        TextView textView = getEditableTextView();
        if (textView != null) (
            (InputMethodManager) getSystemService("input_method")
        ).hideSoftInputFromWindow(textView.getWindowToken(), 0);
    }

    public static boolean isUsingAmazonIap() {
        return usingAmazonIap;
    }

    private static void logd(String paramString) {}

    // Native code:
    private native void nativeAdjustSync(boolean paramBoolean);

    public static native void nativeAppInitJNI();

    public static native void nativeConnectedToIntarweb(boolean paramBoolean);

    private native void nativeResync();

    private void resumeMusic() {
        if (this.backgroundMusicPlaying) {
            resumeBackgroundMusic();
            setVolumeControlStream(3);
        }
        this.refreshMusicState = true;
    }

    public void cacheInterstitial(String paramString) {}

    public void cacheMoreApps() {}

    public boolean canPlayMusic() {
        return (
            this.pm.isScreenOn() &&
            !this.paused &&
            !this.keyguardManager.inKeyguardRestrictedInputMode()
        );
    }

    protected void createEditableTextfield(
        final String text,
        int paramInt1,
        int paramInt2,
        float paramFloat
    ) {
        runOnUiThread(
            new Runnable() {
                public void run() {
                    TextView textView = Aquarium.this.getEditableTextView();
                    if (textView != null) {
                        Aquarium.this.mGLView.insertText(text);
                        textView.setVisibility(0);
                    }
                }
            }
        );
    }

    public boolean checkConnectivity() {
        // nativeConnectedToIntarweb(true);

        if (this.scheduleNativeAdjustSync) {
            // nativeAdjustSync(true);
            this.scheduleNativeAdjustSync = false;
        }

        return true;
    }

    public TextView getEditableTextView() {
        return this.mGLView != null ? this.mGLView.getTextField() : null;
    }

    public Map<String, String> getFlurryCookies() {
        if (this.flurryCookies == null) {
            this.flurryCookies = new HashMap<String, String>();
            this.flurryCookies.put("csudid", JniUtils.getDeviceId());
            logd("CSUDID=" + (String) this.flurryCookies.get("csudid"));
        }
        return this.flurryCookies;
    }

    public int getScreenHeight() {
        return getWindowManager().getDefaultDisplay().getHeight();
    }

    public int getScreenWidth() {
        return getRealWidth();
    }

    public boolean hasCachedInterstitial(String paramString) {
        return true;
    }

    protected void onActivityResult(
        int paramInt1,
        int paramInt2,
        Intent paramIntent
    ) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent);
        JniUtils.onActivityResult(paramInt1, paramInt2, paramIntent);
    }

    // Activity entry point (Main)
    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);

        INSTANCE = this;

        // Show player who modded the game (Custom Class)
        // Welcomer.welcomeNewPlayer(this);
        Cocos2dxActivity.playBackgroundMusic("Sounds/bg-music.wav", true);

        this.handler = new Handler();

        this.keyguardManager = (KeyguardManager) getSystemService("keyguard");
        this.pm = (PowerManager) getSystemService("power");

        setPackageName(getApplication().getPackageName());
        JniUtils.setApplicationContext(getApplicationContext());
        setContentView(R.layout.game_demo);

        this.mGLView = (Cocos2dxGLSurfaceView) findViewById(
            R.id.game_gl_surfaceview
        );
        this.mGLView.setTextField(
            (Cocos2dxEditText) findViewById(R.id.textField)
        );

        nativeAppInitJNI();
        // nativeConnectedToIntarweb(true);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");

        registerReceiver(this.screenReceiver, intentFilter);
    }

    protected Dialog onCreateDialog(int paramInt) {
        int i;

        AlertDialog.Builder builder1 = new AlertDialog.Builder((Context) this);

        if (paramInt == 1) {
            i = R.string.new_version_dialog_message_force_update;
        } else {
            i = R.string.new_version_dialog_message;
        }

        AlertDialog.Builder builder2 = builder1
            .setMessage(i)
            .setCancelable(true)
            .setTitle(R.string.new_version_dialog_title)
            .setPositiveButton(
                R.string.new_version_dialog_ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                        DialogInterface param1DialogInterface,
                        int param1Int
                    ) {
                        Aquarium.this.startStoreIntent();
                    }
                }
            );

        if (paramInt == 1) {
            paramInt = R.string.new_version_dialog_cancel_force_update;
            builder2.setNegativeButton(
                paramInt,
                new DialogInterface.OnClickListener() {
                    public void onClick(
                        DialogInterface param1DialogInterface,
                        int param1Int
                    ) {
                        if (param1Int == 1) Aquarium.INSTANCE.finish();
                        param1DialogInterface.cancel();
                    }
                }
            );

            return (Dialog) builder1.create();
        }

        paramInt = R.string.new_version_dialog_cancel;
        builder2.setNegativeButton(
            paramInt,
            new DialogInterface.OnClickListener() {
                public void onClick(
                    DialogInterface param1DialogInterface,
                    int param1Int
                ) {
                    if (param1Int == 1) Aquarium.INSTANCE.finish();
                    param1DialogInterface.cancel();
                }
            }
        );

        return (Dialog) builder1.create();
    }

    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(this.screenReceiver);
    }

    protected void onNewIntent(Intent paramIntent) {
        super.onNewIntent(paramIntent);
        LocalNotifications.clearNotifications((Context) this);
        resumeBackgroundMusic();
    }

    protected void onPause() {
        this.paused = true;
        if (this.refreshMusicState) {
            this.backgroundMusicPlaying = isBackgroundMusicPlaying();
            pauseBackgroundMusic();
            this.refreshMusicState = false;
        }

        super.onPause();

        if (this.mGLView != null) this.mGLView.onPause();

        if (this.registered) {
            unregisterReceiver(this.receiver);
            this.registered = false;
        }

        if (this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
            this.timer = null;
        }
    }

    protected void onResume() {
        if (this.mGLView != null) this.mGLView.onResume();

        super.onResume();

        hideKeyboard();

        LocalNotifications.clearNotifications((Context) this);

        if (this.keyguardManager.inKeyguardRestrictedInputMode()) {
            IntentFilter intentFilter = new IntentFilter(
                "android.intent.action.USER_PRESENT"
            );
            registerReceiver(this.receiver, intentFilter);
            this.registered = true;
        } else {
            resumeMusic();
        }

        this.handler.postDelayed(
            new Runnable() {
                public void run() {
                    Aquarium.this.showPleaseWaitView(false);
                }
            },
            10000L
        );

        if (this.timer == null) {
            this.timer = new Timer();
            this.timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        Aquarium.this.checkConnectivity();
                    }
                },
                1000L,
                10000L
            );
        }
        this.paused = false;
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        super.onStop();
        this.backgroundMusicPlaying = isBackgroundMusicPlaying();
        pauseBackgroundMusic();
    }

    protected void removeEditableTextfield() {
        runOnUiThread(
            new Runnable() {
                public void run() {
                    TextView textView = Aquarium.this.getEditableTextView();
                    if (textView != null) textView.setVisibility(4);
                    Aquarium.this.hideKeyboard();
                }
            }
        );
    }

    public void setTapPoints(final int pointTotal) {
        this.mGLView.queueEvent(
            new Runnable() {
                public void run() {
                    JniUtils.setTapPoints(pointTotal);
                }
            }
        );
    }

    public void showInterstitial(final String triggerLocation) {
        return;
    }

    public void showMoreApps() {
        return;
    }

    public void showPleaseWaitView(final boolean show) {
        if (this.pleaseWaitViewShowing ^ show) {
            if (show) {
                this.handler.post(
                    new Runnable() {
                        public void run() {
                            Aquarium.this
                                .findViewById(R.id.game_gl_surfaceview)
                                .setVisibility(4);
                            Aquarium.access$602(Aquarium.this, show);
                        }
                    }
                );
                return;
            }
        } else {
            return;
        }
        this.handler.postDelayed(
            new Runnable() {
                public void run() {
                    Aquarium.this
                        .findViewById(R.id.game_gl_surfaceview)
                        .setVisibility(0);
                    Aquarium.access$602(Aquarium.this, show);
                }
            },
            1000L
        );
    }

    public void startStoreIntent() {
        Intent intent = new Intent("android.intent.action.VIEW");
        if (isUsingAmazonIap()) {
            intent.setData(
                Uri.parse(
                    getString(R.string.amazon_store_url, new Object[] {
                        getPackageName(),
                    })
                )
            );
        } else {
            intent.setData(
                Uri.parse(
                    getString(R.string.google_store_url, new Object[] {
                        getPackageName(),
                    })
                )
            );
        }
        startActivity(intent);
    }

    public void trackAction(String paramString) {}

    private static class DialogMessage {

        public String message;
        public String title;

        public DialogMessage(String param1String1, String param1String2) {
            this.title = param1String1;
            this.message = param1String2;
        }
    }

    public static void showMessageBox(String title, String message) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = new DialogMessage(title, message);

        if (Aquarium.INSTANCE != null) {
        }

        if (Aquarium.INSTANCE != null && Aquarium.INSTANCE.handler != null) {
            Aquarium.INSTANCE.handler.sendMessage(msg);
        } else {
            logd("JNI: showMessageBox failed, instance or handler is null.");
        }
    }

    private static boolean access$302(
        Aquarium paramAquarium,
        boolean paramBoolean
    ) {
        paramAquarium.refreshMusicState = paramBoolean;
        return paramBoolean;
    }

    private static boolean access$602(
        Aquarium paramAquarium,
        boolean paramBoolean
    ) {
        paramAquarium.pleaseWaitViewShowing = paramBoolean;
        return paramBoolean;
    }

    // Native facebook trash code
    public static native void androidFBDidLogin();

    public static native void androidFBLoginCancelled();

    public static native void androidFBLoginError();

    public static native void androidFBRecievedAppRequestResponse(
        String paramString
    );

    public static native void androidFBRecievedFriendList(String paramString);

    public static native void androidFBRecievedFriendName(String paramString);

    public static native void androidFBRecievedFriendsUsing(
        String[] paramArrayOfString
    );

    public static native void androidFBRecievedUserInfo(
        String paramString1,
        String paramString2,
        String paramString3
    );

    public static native void androidFBRequestComplete();

    public static native void androidFBRequestError();
}
