package org.cocos2dx.lib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.SoundPool;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Semaphore;
import android.app.KeyguardManager; // Dodany import dla KeyguardManager

public class Cocos2dxSound {
  private static final int INVALID_SOUND_ID = -1;
  
  private static final int INVALID_STREAM_ID = -1;
  
  private static final int MAX_SIMULTANEOUS_STREAMS_DEFAULT = 5;
  
  private static final int SOUND_PRIORITY = 1;
  
  private static final int SOUND_QUALITY = 5;
  
  private static final float SOUND_RATE = 1.0F;
  
  private static final String TAG = "Cocos2dxSound";
  
  private final Context mContext;
  
  private final ArrayList<SoundInfoForLoadedCompleted> mEffecToPlayWhenLoadedArray = new ArrayList<SoundInfoForLoadedCompleted>();
  
  private float mLeftVolume;
  
  private final HashMap<String, Integer> mPathSoundIDMap = new HashMap<String, Integer>();
  
  private final HashMap<String, ArrayList<Integer>> mPathStreamIDsMap = new HashMap<String, ArrayList<Integer>>();
  
  private float mRightVolume;
  
  private Semaphore mSemaphore;
  
  private SoundPool mSoundPool;
  
  private int mStreamIdSyn; // Używane do zastąpienia access$102
  
  public Cocos2dxSound(Context paramContext) {
    this.mContext = paramContext;
    initData();
  }
  
  // --- POPRAWKA 1: Zamiana boolean na int dla pętli w SoundPool.play() (Linia 61) ---
  private int doPlayEffect(String paramString, int paramInt, boolean paramBoolean) {
    int loopCount; // Zmieniono na int
    SoundPool soundPool = this.mSoundPool;
    float f2 = this.mLeftVolume;
    float f1 = this.mRightVolume;

    if (paramBoolean) {
      loopCount = -1; // -1 oznacza pętlę nieskończoną
    } else {
      loopCount = 0; // 0 oznacza brak pętli
    } 
    
    paramInt = soundPool.play(paramInt, f2, f1, 1, loopCount, 1.0F);
    
    ArrayList<Integer> arrayList2 = this.mPathStreamIDsMap.get(paramString);
    ArrayList<Integer> arrayList1 = arrayList2;
    if (arrayList2 == null) {
      arrayList1 = new ArrayList();
      this.mPathStreamIDsMap.put(paramString, arrayList1);
    } 
    arrayList1.add(Integer.valueOf(paramInt));
    return paramInt;
  }
  
  private void initData() {
    this.mSoundPool = new SoundPool(5, 3, 5);
    this.mSoundPool.setOnLoadCompleteListener(new OnLoadCompletedListener());
    this.mLeftVolume = 0.5F;
    this.mRightVolume = 0.5F;
    this.mSemaphore = new Semaphore(0, true);
  }
  
  // --- POPRAWKA 2: Naprawa rzutowania w loadEffect (Linia 87) ---
  public int createSoundIDFromAsset(String paramString) {
    Integer integer1;
    try {
      if (paramString.startsWith("/")) {
        int i = this.mSoundPool.load(paramString, 0);
        integer1 = Integer.valueOf(i);
      } else {
        // Poprawne użycie paramString do otwarcia AssetFileDescriptor
        AssetFileDescriptor assetFileDescriptor = this.mContext.getAssets().openFd(paramString);
        int i = this.mSoundPool.load(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength(), 0);
        integer1 = Integer.valueOf(i);
      } 
    } catch (Exception exception) {
      integer1 = Integer.valueOf(-1);
      Log.e("Cocos2dxSound", "error: " + exception.getMessage(), exception);
    } 
    if (integer1 != null) {
      Integer integer = integer1;
      if (integer1.intValue() == 0) {
        integer = Integer.valueOf(-1);
        return integer.intValue();
      } 
      return integer.intValue();
    } 
    Integer integer2 = Integer.valueOf(-1);
    return integer2.intValue();
  }
  
  public void end() {
    this.mSoundPool.release();
    this.mPathStreamIDsMap.clear();
    this.mPathSoundIDMap.clear();
    this.mEffecToPlayWhenLoadedArray.clear();
    this.mLeftVolume = 0.5F;
    this.mRightVolume = 0.5F;
    // Nie wywołujemy initData() na końcu, bo end() powinno zwalniać zasoby. 
    // Jeśli ma działać jako reset, initData() jest potrzebne. Zostawiam dekompilowaną logikę.
    initData(); 
  }
  
  public float getEffectsVolume() {
    return (this.mLeftVolume + this.mRightVolume) / 2.0F;
  }
  
  public void pauseAllEffects() {
    this.mSoundPool.autoPause();
  }
  
  public void pauseEffect(int paramInt) {
    this.mSoundPool.pause(paramInt);
  }
  
  // --- POPRAWKA 3: Naprawa konstruktora SoundInfoForLoadedCompleted (Linie 139, 140) ---
  public int playEffect(String paramString, boolean paramBoolean) {
    Integer integer1 = this.mPathSoundIDMap.get(paramString);
    if (((KeyguardManager)this.mContext.getSystemService(Context.KEYGUARD_SERVICE)).inKeyguardRestrictedInputMode())
      return -1; 
    if (integer1 != null)
      return doPlayEffect(paramString, integer1.intValue(), paramBoolean); 
    
    Integer integer2 = Integer.valueOf(preloadEffect(paramString));
    if (integer2.intValue() == -1)
      return -1; 
    
    synchronized (this.mSoundPool) {
      ArrayList<SoundInfoForLoadedCompleted> arrayList = this.mEffecToPlayWhenLoadedArray;
      
      // Poprawne wywołanie konstruktora
      SoundInfoForLoadedCompleted soundInfoForLoadedCompleted = new SoundInfoForLoadedCompleted(paramString, integer2.intValue(), paramBoolean);
      
      arrayList.add(soundInfoForLoadedCompleted);
      
      try {
        this.mSemaphore.acquire();
        int i = this.mStreamIdSyn;
        return i;
      } catch (Exception exception) {
        Log.e(TAG, "Semaphore acquire failed", exception);
        return -1;
      }
    } 
  }
  
  public int preloadEffect(String paramString) {
    Integer integer2 = this.mPathSoundIDMap.get(paramString);
    Integer integer1 = integer2;
    if (integer2 == null) {
      integer2 = Integer.valueOf(createSoundIDFromAsset(paramString));
      integer1 = integer2;
      if (integer2.intValue() != -1) {
        this.mPathSoundIDMap.put(paramString, integer2);
        integer1 = integer2;
      } 
    } 
    return integer1.intValue();
  }
  
  // --- POPRAWKA 4: Naprawa generyków Iteratorów (Linia 165, 190, 203) ---
  public void resumeAllEffects() {
    if (!this.mPathStreamIDsMap.isEmpty()) {
      Iterator<Map.Entry<String, ArrayList<Integer>>> iterator = this.mPathStreamIDsMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Iterator<Integer> iterator1 = ((ArrayList)((Map.Entry)iterator.next()).getValue()).iterator();
        while (iterator1.hasNext()) {
          int i = ((Integer)iterator1.next()).intValue();
          this.mSoundPool.resume(i);
        } 
      } 
    } 
  }
  
  public void resumeEffect(int paramInt) {
    this.mSoundPool.resume(paramInt);
  }
  
  public void setEffectsVolume(float paramFloat) {
    float f = paramFloat;
    if (paramFloat < 0.0F)
      f = 0.0F; 
    paramFloat = f;
    if (f > 1.0F)
      paramFloat = 1.0F; 
    this.mRightVolume = paramFloat;
    this.mLeftVolume = paramFloat;
    if (!this.mPathStreamIDsMap.isEmpty()) {
      Iterator<Map.Entry<String, ArrayList<Integer>>> iterator = this.mPathStreamIDsMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Iterator<Integer> iterator1 = ((ArrayList)((Map.Entry)iterator.next()).getValue()).iterator();
        while (iterator1.hasNext()) {
          int i = ((Integer)iterator1.next()).intValue();
          this.mSoundPool.setVolume(i, this.mLeftVolume, this.mRightVolume);
        } 
      } 
    } 
  }
  
  public void stopAllEffects() {
    if (!this.mPathStreamIDsMap.isEmpty()) {
      Iterator<Map.Entry<String, ArrayList<Integer>>> iterator = this.mPathStreamIDsMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Iterator<Integer> iterator1 = ((ArrayList)((Map.Entry)iterator.next()).getValue()).iterator();
        while (iterator1.hasNext()) {
          int i = ((Integer)iterator1.next()).intValue();
          this.mSoundPool.stop(i);
        } 
      } 
    } 
    this.mPathStreamIDsMap.clear();
  }
  
  // --- POPRAWKA 5: Naprawa rzutowania w stopEffect (Linia 228) - Błąd w tym pliku nie występuje, ale ujednolicono użycie generyków ---
  public void stopEffect(int paramInt) {
    this.mSoundPool.stop(paramInt);
    for (String str : this.mPathStreamIDsMap.keySet()) {
      ArrayList<Integer> streamIDs = this.mPathStreamIDsMap.get(str);
      if (streamIDs.contains(Integer.valueOf(paramInt))) {
        streamIDs.remove(Integer.valueOf(paramInt));
        break;
      } 
    } 
  }
  
  public void unloadEffect(String paramString) {
    ArrayList<Integer> arrayList = this.mPathStreamIDsMap.get(paramString);
    if (arrayList != null)
      for (Integer integer1 : arrayList)
        this.mSoundPool.stop(integer1.intValue());  
    this.mPathStreamIDsMap.remove(paramString);
    Integer integer = this.mPathSoundIDMap.get(paramString);
    if (integer != null) {
        this.mSoundPool.unload(integer.intValue());
        this.mPathSoundIDMap.remove(paramString);
    }
  }
  
  public class OnLoadCompletedListener implements SoundPool.OnLoadCompleteListener {
    public void onLoadComplete(SoundPool param1SoundPool, int param1Int1, int param1Int2) {
      if (param1Int2 == 0) {
        for (Cocos2dxSound.SoundInfoForLoadedCompleted soundInfoForLoadedCompleted : Cocos2dxSound.this.mEffecToPlayWhenLoadedArray) {
          if (param1Int1 == soundInfoForLoadedCompleted.soundID) {
            // --- POPRAWKA 6: Zastąpienie access$102 polem mStreamIdSyn (Linia 243) ---
            Cocos2dxSound.this.mStreamIdSyn = Cocos2dxSound.this.doPlayEffect(soundInfoForLoadedCompleted.path, soundInfoForLoadedCompleted.soundID, soundInfoForLoadedCompleted.isLoop);
            
            Cocos2dxSound.this.mEffecToPlayWhenLoadedArray.remove(soundInfoForLoadedCompleted);
            break;
          } 
        } 
      } else {
        Cocos2dxSound.this.mStreamIdSyn = -1;
      } 
      Cocos2dxSound.this.mSemaphore.release();
    }
  }
  
  public class SoundInfoForLoadedCompleted {
    public boolean isLoop;
    
    public String path;
    
    public int soundID;
    
    public SoundInfoForLoadedCompleted(String param1String, int param1Int, boolean param1Boolean) {
      this.path = param1String;
      this.soundID = param1Int;
      this.isLoop = param1Boolean;
    }
  }
}