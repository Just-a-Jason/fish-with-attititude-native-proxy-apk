package org.cocos2dx.lib;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

public class Cocos2dxMusic {
  private static final String TAG = "Cocos2dxMusic";
  
  private MediaPlayer mBackgroundMediaPlayer;
  
  private Context mContext;
  
  private String mCurrentPath;
  
  private boolean mIsPaused;
  
  private float mLeftVolume;
  
  private float mRightVolume;
  
  public Cocos2dxMusic(Context paramContext) {
    this.mContext = paramContext;
    initData();
  }
  
  private MediaPlayer createMediaplayerFromAssets(String musicFilePath) {
    MediaPlayer mediaPlayer = new MediaPlayer();
    MediaPlayer result = null; 

    try {
      if (musicFilePath.startsWith("/")) {
        mediaPlayer.setDataSource(musicFilePath);
      } else {
        AssetFileDescriptor assetFileDescriptor = this.mContext.getAssets().openFd(musicFilePath);
        mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
      } 
    
      mediaPlayer.prepare();
      mediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume);
      result = mediaPlayer;
    
    } catch (Exception exception) {
      result = null; 
      Log.e("Cocos2dxMusic", "error: " + exception.getMessage(), exception);
    } 
    
    return result; 
  }
  
  private void initData() {
    this.mLeftVolume = 0.5F;
    this.mRightVolume = 0.5F;
    this.mBackgroundMediaPlayer = null;
    this.mIsPaused = false;
    this.mCurrentPath = null;
  }
  
  public void end() {
    if (this.mBackgroundMediaPlayer != null)
      this.mBackgroundMediaPlayer.release(); 
    initData();
  }
  
  public float getBackgroundVolume() {
    return (this.mBackgroundMediaPlayer != null) ? ((this.mLeftVolume + this.mRightVolume) / 2.0F) : 0.0F;
  }
  
  public boolean isBackgroundMusicPlaying() {
    return (this.mBackgroundMediaPlayer == null) ? false : this.mBackgroundMediaPlayer.isPlaying();
  }
  
  public void pauseBackgroundMusic() {
    if (this.mBackgroundMediaPlayer != null && this.mBackgroundMediaPlayer.isPlaying()) {
      this.mBackgroundMediaPlayer.pause();
      this.mIsPaused = true;
    } 
  }
  
  public void playBackgroundMusic(String pathToMusicFile, boolean looping) {
    if (this.mCurrentPath == null) {
      this.mBackgroundMediaPlayer = createMediaplayerFromAssets(pathToMusicFile);
      this.mCurrentPath = pathToMusicFile;
    } else if (!this.mCurrentPath.equals(pathToMusicFile)) {
      if (this.mBackgroundMediaPlayer != null)
        this.mBackgroundMediaPlayer.release(); 
      this.mBackgroundMediaPlayer = createMediaplayerFromAssets(pathToMusicFile);
      this.mCurrentPath = pathToMusicFile;
    } 
    if (this.mBackgroundMediaPlayer == null) {
      Log.e("Cocos2dxMusic", "playBackgroundMusic: background media player is null");
      return;
    } 
    this.mBackgroundMediaPlayer.stop();
    this.mBackgroundMediaPlayer.setLooping(looping);
    try {
      this.mBackgroundMediaPlayer.prepare();
      this.mBackgroundMediaPlayer.seekTo(0);
      this.mBackgroundMediaPlayer.start();
      this.mIsPaused = false;
    } catch (Exception exception) {
      Log.e("Cocos2dxMusic", "playBackgroundMusic: error state");
    } 
  }
  
  public void preloadBackgroundMusic(String pathToMusicFile) {
    if (this.mCurrentPath == null || !this.mCurrentPath.equals(pathToMusicFile)) {
      if (this.mBackgroundMediaPlayer != null)
        this.mBackgroundMediaPlayer.release(); 
      this.mBackgroundMediaPlayer = createMediaplayerFromAssets(pathToMusicFile);
      this.mCurrentPath = pathToMusicFile;
    } 
  }
  
  public void resumeBackgroundMusic() {
    if (this.mBackgroundMediaPlayer != null && this.mIsPaused) {
      this.mBackgroundMediaPlayer.start();
      this.mIsPaused = false;
    } 
  }
  
  public void rewindBackgroundMusic() {
    if (this.mBackgroundMediaPlayer != null) {
      this.mBackgroundMediaPlayer.stop();
      try {
        this.mBackgroundMediaPlayer.prepare();
        this.mBackgroundMediaPlayer.seekTo(0);
        this.mBackgroundMediaPlayer.start();
        this.mIsPaused = false;
      } catch (Exception exception) {
        Log.e("Cocos2dxMusic", "rewindBackgroundMusic: error state");
      } 
    } 
  }
  
  public void setBackgroundVolume(float paramFloat) {
    float f = paramFloat;
    if (paramFloat < 0.0F)
      f = 0.0F; 
    paramFloat = f;
    if (f > 1.0F)
      paramFloat = 1.0F; 
    this.mRightVolume = paramFloat;
    this.mLeftVolume = paramFloat;
    if (this.mBackgroundMediaPlayer != null)
      this.mBackgroundMediaPlayer.setVolume(this.mLeftVolume, this.mRightVolume); 
  }
  
  public void stopBackgroundMusic() {
    if (this.mBackgroundMediaPlayer != null) {
      this.mBackgroundMediaPlayer.stop();
      this.mIsPaused = false;
    } 
  }
}


/* Location:              /home/jason/Pobrane/fish.jar!/org/cocos2dx/lib/Cocos2dxMusic.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */