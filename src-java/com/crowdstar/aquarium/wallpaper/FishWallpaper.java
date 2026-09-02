package com.crowdstar.aquarium.wallpaper;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.cocos2dx.lib.Cocos2dxGLSurfaceView;

public class FishWallpaper extends WallpaperService {
  private static final String TAG = "FWA_WallpaperService";
  
  private Cocos2dxGLSurfaceView mGLView;
  
  private Bitmap loadAssetBitmap(String paramString) throws IOException {
    return BitmapFactory.decodeStream(getAssets().open(paramString));
  }
  
  private Bitmap loadFileBitmap(String paramString) throws FileNotFoundException {
    File file = new File(paramString);
    if (!file.exists())
      throw new FileNotFoundException(file.getAbsolutePath()); 
    return BitmapFactory.decodeFile(paramString);
  }
  
  public WallpaperService.Engine onCreateEngine() {
    return new FishWallpaperEngine();
  }
  
  class FishDefinition {
    private static final float BORDER = 50.0F;
    
    private static final float CLICK_RAD = 150.0F;
    
    private static final float SPEED = 0.2F;
    
    Bitmap bitmap;
    
    float debugvelx = 0.0F;
    
    float debugvely = 0.0F;
    
    private long dodgeTime;
    
    private float dodgeVelX = 0.0F;
    
    private float dodgeVelY = 0.0F;
    
    private float maxHeight;
    
    private float maxWidth;
    
    Random random = new Random();
    
    long time = 0L;
    
    float x = 0.0F;
    
    float y = 0.0F;
    
    FishDefinition(Bitmap param1Bitmap, float param1Float1, float param1Float2, float param1Float3, float param1Float4) {
      this.bitmap = param1Bitmap;
      this.x = param1Float1;
      this.y = param1Float2;
      this.maxWidth = param1Float3;
      this.maxHeight = param1Float4;
      this.debugvelx = (this.random.nextFloat() - 0.5F) * 0.2F;
      this.debugvely = (this.random.nextFloat() - 0.5F) * 0.2F;
    }
    
    private void dodge(double param1Double, float param1Float) {
      this.dodgeTime = System.currentTimeMillis();
      this.dodgeVelX = (this.random.nextFloat() - 0.5F) * 0.8F;
      this.dodgeVelY = (this.random.nextFloat() - 0.5F) * 0.8F;
    }
    
    void debugAnimate() {
      long l2 = System.currentTimeMillis() - this.time;
      this.time = System.currentTimeMillis();
      long l1 = l2;
      if (l2 > 100L)
        l1 = 100L; 
      if (System.currentTimeMillis() - this.dodgeTime < 500L) {
        this.x += this.dodgeVelX * (float)l1;
        this.y += this.dodgeVelY * (float)l1;
        if (this.x < 50.0F || this.x + this.bitmap.getWidth() > this.maxWidth - 50.0F)
          this.dodgeTime = 0L; 
        if (this.y < 50.0F || this.y + this.bitmap.getHeight() > this.maxHeight - 50.0F)
          this.dodgeTime = 0L; 
        return;
      } 
      this.x += this.debugvelx * (float)l1;
      this.y += this.debugvely * (float)l1;
      if (this.x < 50.0F || this.x + this.bitmap.getWidth() > this.maxWidth - 50.0F)
        this.debugvelx = -this.debugvelx; 
      if (this.y < 50.0F || this.y + this.bitmap.getHeight() > this.maxHeight - 50.0F)
        this.debugvely = -this.debugvely; 
    }
    
    public void handleClick(float param1Float1, float param1Float2) {
      if (Math.abs((this.bitmap.getWidth() / 2) + param1Float1 - this.x) < 150.0F && Math.abs((this.bitmap.getHeight() / 2) + param1Float2 - this.y) < 150.0F)
        dodge(param1Float1, param1Float2); 
    }
  }
  
  private class FishWallpaperEngine extends WallpaperService.Engine {
    private Bitmap backgroundBitmap;
    
    private final Runnable drawRunner = new Runnable() {
        public void run() {
          FishWallpaper.FishWallpaperEngine.this.draw();
        }
      };
    
    private Rect dstRect;
    
    private Collection<FishWallpaper.FishDefinition> fishes = Collections.emptyList();
    
    private final Handler handler = new Handler();
    
    private Paint paint = new Paint();
    
    private Collection<FishWallpaper.PropDefinition> props = Collections.emptyList();
    
    private int wallpaperHeight;
    
    private int wallpaperWidth;
    
    private int xPixelOffset;
    
    private int yPixelOffset;
    
    private FishWallpaperEngine() {
      super();

      this.paint.setAntiAlias(true);
      this.paint.setColor(-1);
      this.paint.setStyle(Paint.Style.STROKE);
      this.paint.setStrokeJoin(Paint.Join.ROUND);
      this.paint.setStrokeWidth(10.0F);
      
      try {
        this.backgroundBitmap = FishWallpaper.this.loadAssetBitmap("BG_Wallpaper_Arch.png");
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(FishWallpaper.this.getApplicationContext());
        this.wallpaperHeight = wallpaperManager.getDesiredMinimumHeight();
        this.wallpaperWidth = wallpaperManager.getDesiredMinimumWidth();
        Rect rect = new Rect();
        
        
        rect.set(0, 0, this.wallpaperWidth, this.wallpaperHeight);
        
        this.dstRect = rect;
        this.props = generateRandomProps(20);
        this.fishes = generateRandomFishes(5);
      } catch (IOException iOException) {
        Log.e("FWA_WallpaperService", iOException.getMessage(), iOException);
      } 
    }
    
    private void draw() {
      SurfaceHolder surfaceHolder = getSurfaceHolder();
      Canvas canvas = null;
      try {
        canvas = surfaceHolder.lockCanvas(); 
        
        if (canvas != null) {
            canvas.drawBitmap(this.backgroundBitmap, null, this.dstRect, null);
            
            
        }
        
      } finally {
        if (canvas != null)
          surfaceHolder.unlockCanvasAndPost(canvas); 
      } 
      

      this.handler.removeCallbacks(this.drawRunner);
      if (isVisible())
        this.handler.postDelayed(this.drawRunner, 16L); 
    }
    
    private Collection<FishWallpaper.FishDefinition> generateRandomFishes(int param1Int) throws IOException {
      HashSet<FishWallpaper.FishDefinition> hashSet = new HashSet();
      hashSet.add(new FishWallpaper.FishDefinition(FishWallpaper.this.loadAssetBitmap("Fish_Tier1_Happy_baby_preview-hd.png"), 100.0F, 100.0F, this.wallpaperWidth, this.wallpaperHeight));
      hashSet.add(new FishWallpaper.FishDefinition(FishWallpaper.this.loadAssetBitmap("Fish_Tier1_Mean_small_baby-hd.png"), 200.0F, 400.0F, this.wallpaperWidth, this.wallpaperHeight));
      return hashSet;
    }
    
    private Collection<FishWallpaper.PropDefinition> generateRandomProps(int param1Int) throws FileNotFoundException {
      String str = getPropPath();
      File file = new File(str);
      if (!file.exists() || !file.isDirectory())
        throw new FileNotFoundException(str); 
      String[] arrayOfString = file.list(new FilenameFilter() {
            public boolean accept(File param2File, String param2String) {
              return param2String.toLowerCase().endsWith(".png");
            }
          });
      if (arrayOfString.length <= 0) {
        Log.e("FWA_WallpaperService", "Cannot find props, TODO");
        throw new FileNotFoundException(str);
      } 
      HashSet<FishWallpaper.PropDefinition> hashSet = new HashSet();
      Random random = new Random();
      for (byte b = 0; b < param1Int; b++) {
        File file1 = new File(str, arrayOfString[random.nextInt(arrayOfString.length)]);
        Bitmap bitmap = FishWallpaper.this.loadFileBitmap(file1.getAbsolutePath());
        int m = random.nextInt(this.wallpaperWidth - bitmap.getWidth());
        int i = this.wallpaperHeight;
        int j = bitmap.getHeight();
        int k = random.nextInt(200);
        hashSet.add(new FishWallpaper.PropDefinition(bitmap, m, (i - j - k)));
      } 
      return hashSet;
    }
    
    private String getPropPath() {
      return "/data/data/" + FishWallpaper.this.getPackageName() + "/props/sd/";
    }
    
    public void handleClick(float param1Float1, float param1Float2) {
      Iterator<FishWallpaper.FishDefinition> iterator = this.fishes.iterator();
      while (iterator.hasNext())
        ((FishWallpaper.FishDefinition)iterator.next()).handleClick(param1Float1 - this.xPixelOffset, param1Float2 - this.yPixelOffset); 
    }
    
    public void onCreate(SurfaceHolder param1SurfaceHolder) {
      super.onCreate(param1SurfaceHolder);
      this.handler.post(this.drawRunner);
    }
    
    public void onOffsetsChanged(float param1Float1, float param1Float2, float param1Float3, float param1Float4, int param1Int1, int param1Int2) {
      this.xPixelOffset = param1Int1;
      this.yPixelOffset = param1Int2;
      this.dstRect.set(param1Int1, param1Int2, this.wallpaperWidth + param1Int1, this.wallpaperHeight + param1Int2);
      super.onOffsetsChanged(param1Float1, param1Float2, param1Float3, param1Float4, param1Int1, param1Int2);
      draw();
    }
    
    public void onSurfaceChanged(SurfaceHolder param1SurfaceHolder, int param1Int1, int param1Int2, int param1Int3) {
      super.onSurfaceChanged(param1SurfaceHolder, param1Int1, param1Int2, param1Int3);
    }
    
    public void onTouchEvent(MotionEvent param1MotionEvent) {
      switch (param1MotionEvent.getAction()) {
        default:
          super.onTouchEvent(param1MotionEvent);
          return;
        case 0:
          break;
      } 
      handleClick(param1MotionEvent.getX(), param1MotionEvent.getY());
    }
    
    public void onVisibilityChanged(boolean param1Boolean) {
      super.onVisibilityChanged(param1Boolean);
      if (param1Boolean) {
        this.handler.post(this.drawRunner);
        return;
      } 
      this.handler.removeCallbacks(this.drawRunner);
    }
  }

  private final Runnable fishRunnable = new Runnable() {
    public void run() {
    }
  };
  
  private final FilenameFilter fishFileNameFilter = new FilenameFilter() {
    public boolean accept(File param1File, String param1String) {
      return param1String.toLowerCase().endsWith(".png");
    }
  }; 
  
  class PropDefinition {
    Bitmap bitmap;
    
    float x = 0.0F;
    
    float y = 0.0F;
    
    PropDefinition(Bitmap param1Bitmap, float param1Float1, float param1Float2) {
      this.bitmap = param1Bitmap;
      this.x = param1Float1;
      this.y = param1Float2;
    }
  }
}