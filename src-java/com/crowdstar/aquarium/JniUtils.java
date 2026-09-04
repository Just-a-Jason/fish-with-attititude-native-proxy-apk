package com.crowdstar.aquarium;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.crowdstar.aquarium.util.LocalNotifications;
import java.util.Calendar;
import java.util.UUID;

public class JniUtils {

    private static final String TAG = "FWA_JniUtils";

    static Context applicationContext;

    private static native void addProductData(
        String paramString1,
        String paramString2,
        String paramString3,
        String paramString4,
        String paramString5
    );

    public static void appRequestDialog(
        String paramString1,
        String paramString2,
        String paramString3,
        String paramString4
    ) {}

    public static void applicationIsReadyToRender() {
        Aquarium.INSTANCE.showPleaseWaitView(false);
    }

    public static void authorize(String paramString) {}

    public static void authorizeUsingFacebookApp(String paramString) {}

    public static void cacheInterstitial(String paramString) {
        Aquarium.INSTANCE.cacheInterstitial(paramString);
    }

    public static void cacheMoreApps() {
        Aquarium.INSTANCE.cacheMoreApps();
    }

    public static boolean canPlayMusic() {
        return Aquarium.INSTANCE.canPlayMusic();
    }

    public static boolean canSendMail() {
        return true;
    }

    public static boolean canSendText() {
        boolean bool2 = false;
        TelephonyManager telephonyManager =
            (TelephonyManager) getApplicationContext().getSystemService(
                "phone"
            );
        boolean bool1 = bool2;
        if (telephonyManager != null) {
            bool1 = bool2;
            if (telephonyManager.getSimState() == 5) bool1 = true;
        }
        return bool1;
    }

    public static boolean canTweetStatus() {
        return true;
    }

    public static void cancelNotifications() {
        LocalNotifications.cancelNotifications(getApplicationContext());
    }

    public static void createEditableTextfield(
        String paramString,
        int paramInt1,
        int paramInt2,
        float paramFloat
    ) {
        Aquarium.INSTANCE.createEditableTextfield(
            paramString,
            paramInt1,
            paramInt2,
            paramFloat
        );
    }

    public static void deleteAppRequest(String paramString) {}

    public static native void editableTextfieldDismissed(String paramString);

    protected static native void finishTransaction(
        String paramString1,
        int paramInt,
        String paramString2
    );

    public static void getAppRequests() {}

    public static Context getApplicationContext() {
        return applicationContext;
    }

    // Current modified version
    public static String getApplicationVersion() {
        return "1.0.39";
    }

    public static String getDeviceId() {
        return Build.SERIAL;
    }

    public static String getMACAddress() {
        String str2 = (
            (WifiManager) getApplicationContext().getSystemService("wifi")
        )
            .getConnectionInfo()
            .getMacAddress();
        String str1 = str2;
        if (str2 == null) str1 = "d34db33fd34db33f";
        return str1;
    }

    public static String getODIN() {
        return Settings.Secure.getString(
            getApplicationContext().getContentResolver(),
            "android_id"
        );
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getPlatform() {
        return Build.MODEL;
    }

    public static void getProductsData() {}

    public static int getScreenHeight() {
        return Aquarium.INSTANCE.getScreenHeight();
    }

    public static int getScreenWidth() {
        return Aquarium.INSTANCE.getScreenWidth();
    }

    public static String getStringUserDefault(String paramString) {
        return PreferenceManager.getDefaultSharedPreferences(
            getApplicationContext()
        ).getString(paramString, "");
    }

    // Unused empty function
    public static void getTapPoints() {}

    public static String getUUID(
        String paramString1,
        String paramString2,
        boolean paramBoolean
    ) {
        String str1 = new String();
        SharedPreferences sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
            );
        String str2 = sharedPreferences.getString(paramString1, null);
        paramString2 = str2;

        if (str2 == null) {
            str2 = UUID.randomUUID().toString();
            paramString2 = str2;
            if (paramBoolean) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(paramString1, str2);
                editor.commit();
                str1 = str2;
            }
        }

        return str1;
    }

    // Deprected unused FacebookApi functions
    public static String getUserFacebookEmail() {
        return new String();
    }

    public static String getUserFacebookId() {
        return new String();
    }

    public static String getUserFacebookName() {
        return new String();
    }

    public static boolean hasCachedInterstitial(String paramString) {
        return Aquarium.INSTANCE.hasCachedInterstitial(paramString);
    }

    public static boolean isLoggedIntoFacebook() {
        return false;
    }

    public static void onActivityResult(
        int paramInt1,
        int paramInt2,
        Intent paramIntent
    ) {}

    public static void postAchievement(
        String paramString1,
        String paramString2
    ) {}

    public static void postFacebookScore(int paramInt) {}

    public static void removeEditableTextfield() {
        Aquarium.INSTANCE.removeEditableTextfield();
    }

    public static void requestUserFriendList() {}

    public static void requestUserFriendName(String paramString) {}

    public static void requestUserFriendsUsingApp() {}

    public static void requestUserInfo() {}

    public static void scheduleFutureNotification(
        String paramString1,
        String paramString2,
        int paramInt,
        String paramString3
    ) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(12, paramInt);
        LocalNotifications.scheduleFutureNotification(
            getApplicationContext(),
            paramString1,
            paramString2,
            calendar.getTimeInMillis(),
            paramString3
        );
    }

    public static void sendCommand(int paramInt) {}

    private static void sendCustomTweet() {
        sendEasyTweet();
    }

    public static void sendEasyTweet() {
        Aquarium aquarium = Aquarium.INSTANCE;
        String str = aquarium.getString(2131034144, new Object[] {
            aquarium.getString(2131034114),
            aquarium.getPackageName(),
        });
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.TEXT", str);
        aquarium.startActivity(
            Intent.createChooser(intent, aquarium.getString(2131034143))
        );
    }

    public static void sendFacebookWallpost(
        String paramString1,
        String paramString2,
        String paramString3,
        String paramString4
    ) {}

    private static void sendMail(
        Activity paramActivity,
        String paramString1,
        String paramString2,
        String paramString3
    ) {
        Intent intent = new Intent("android.intent.action.SEND");
        String[] arrayOfString = new String[0];
        if (paramString1 != null) {
            arrayOfString = new String[1];
            arrayOfString[0] = paramString1;
        }
        intent.setType("text/plain");
        intent.putExtra("android.intent.extra.EMAIL", arrayOfString);
        intent.putExtra("android.intent.extra.SUBJECT", paramString2);
        intent.putExtra("android.intent.extra.TEXT", paramString3);
        paramActivity.startActivity(
            Intent.createChooser(intent, paramActivity.getString(2131034139))
        );
    }

    public static void sendText() {
        Aquarium aquarium = Aquarium.INSTANCE;
        Intent intent = new Intent(
            "android.intent.action.SENDTO",
            Uri.parse("smsto:")
        );
        intent.putExtra(
            "sms_body",
            aquarium.getString(2131034136, new Object[] {
                aquarium.getString(2131034114),
                aquarium.getPackageName(),
            })
        );
        aquarium.startActivity(intent);
    }

    public static void setApplicationContext(Context paramContext) {
        applicationContext = paramContext;
    }

    public static void setStringUserDefault(
        String paramString1,
        String paramString2
    ) {
        SharedPreferences.Editor editor =
            PreferenceManager.getDefaultSharedPreferences(
                getApplicationContext()
            ).edit();
        editor.putString(paramString1, paramString2);
        editor.commit();
    }

    public static native void setTapPoints(int paramInt);

    public static void showDefaultEarnedCurrencyAlert() {}

    // Unknown at this point
    public static void showHelpMailComposer() {
        // Log.w("FWA_JniUtils", "TODO: fbid, systeminfo etc.");
        // String str5 = getApplicationVersion();
        // Context context = getApplicationContext();
        // String str1 = context.getString(2131034140);
        // String str2 = context.getString(2131034141, new Object[] { context.getString(2131034114), context.getString(2131034115) });
        // String str3 = context.getString(2131034142, new Object[] { context.getString(2131034114), context.getString(2131034115), str5, "ECONOMY", "MACHINE", str4, "PID" });
        // sendMail((Activity)Aquarium.INSTANCE, str1, str2, str3);
    }

    public static void showInterstitial(String paramString) {
        Aquarium.INSTANCE.showInterstitial(paramString);
    }

    public static void showMailComposer() {
        Context context = getApplicationContext();
        String str1 = context.getString(2131034137, new Object[] {
            context.getString(2131034114),
        });
        String str2 = context.getString(2131034138, new Object[] {
            context.getString(2131034114),
            context.getPackageName(),
        });
        sendMail((Activity) Aquarium.INSTANCE, null, str1, str2);
    }

    public static void showMoreApps() {
        Aquarium.INSTANCE.showMoreApps();
    }

    public static void showNewVersionAlert(final boolean forceUpdate) {
        Aquarium.INSTANCE.runOnUiThread(
            new Runnable() {
                public void run() {
                    byte b;
                    Aquarium aquarium = Aquarium.INSTANCE;
                    if (forceUpdate) {
                        b = 1;
                    } else {
                        b = 2;
                    }
                    aquarium.showDialog(b);
                }
            }
        );
    }

    public static void showOffers() {}

    // Usefull in the future to open native android Webview Service
    public static void showWebView(String url) {
        Uri uri = null;

        Intent intent = new Intent("android.intent.action.VIEW");
        Object object = null;

        try {
            Uri uri1 = Uri.parse(url);
            uri = uri1;
        } catch (Exception exception) {}

        if (uri != null) {
            intent.setData(uri);
            Aquarium.INSTANCE.startActivity(intent);
        }
    }

    public static void spendTapPoints(int paramInt) {}

    public static void startFacebookLogin() {}

    public static void startPostToFacebook(
        String paramString1,
        String paramString2
    ) {}

    public static void startPurchase(String paramString) {}

    public static void trackAction(String paramString) {
        Aquarium.INSTANCE.trackAction(paramString);
    }

    public static void uploadFileToPhotoAlbumWithMessage(
        String paramString1,
        String paramString2
    ) {}
}
