package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Typeface;
import java.util.Hashtable;

public class Cocos2dxTypefaces {
  private static final Hashtable<String, Typeface> cache = new Hashtable<String, Typeface>();
  
  public static Typeface get(Context paramContext, String paramString) {
    synchronized (cache) {
      if (!cache.containsKey(paramString)) {
        Typeface typeface = Typeface.createFromAsset(paramContext.getAssets(), paramString);
        cache.put(paramString, typeface);
      } 
      return cache.get(paramString);
    } 
  }
}


/* Location:              /home/jason/Pobrane/fish.jar!/org/cocos2dx/lib/Cocos2dxTypefaces.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */