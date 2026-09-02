package org.cocos2dx.lib;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
// Dodatkowe importy, jeśli były, zachowaj je
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Cocos2dxGLSurfaceView extends GLSurfaceView {
  private static final int HANDLER_CLOSE_IME_KEYBOARD = 3;
  
  private static final int HANDLER_OPEN_IME_KEYBOARD = 2;
  
  private static final String TAG = Cocos2dxGLSurfaceView.class.getCanonicalName();
  
  private static final boolean debug = false;
  
  private static Handler handler;
  
  private static Cocos2dxGLSurfaceView mainView;
  
  private static TextInputWraper textInputWraper;
  
  private Cocos2dxRenderer mRenderer;
  
  private Cocos2dxEditText mTextField;
  
  public Cocos2dxGLSurfaceView(Context paramContext) {
    super(paramContext);
    initView();
  }
  
  public Cocos2dxGLSurfaceView(Context paramContext, AttributeSet paramAttributeSet) {
    super(paramContext, paramAttributeSet);
    initView();
  }
  
  public static void closeIMEKeyboard() {
    Message message = new Message();
    message.what = 3;
    handler.sendMessage(message);
  }
  
  private void dumpEvent(MotionEvent paramMotionEvent) {
    StringBuilder stringBuilder = new StringBuilder();
    int j = paramMotionEvent.getAction();
    int i = j & 0xFF;
    (new String[10])[0] = "DOWN";
    (new String[10])[1] = "UP";
    (new String[10])[2] = "MOVE";
    (new String[10])[3] = "CANCEL";
    (new String[10])[4] = "OUTSIDE";
    (new String[10])[5] = "POINTER_DOWN";
    (new String[10])[6] = "POINTER_UP";
    (new String[10])[7] = "7?";
    (new String[10])[8] = "8?";
    (new String[10])[9] = "9?";
    stringBuilder.append("event ACTION_").append((new String[10])[i]);
    if (i == 5 || i == 6) {
      stringBuilder.append("(pid ").append(j >> 8);
      stringBuilder.append(")");
    } 
    stringBuilder.append("[");
    for (i = 0; i < paramMotionEvent.getPointerCount(); i++) {
      stringBuilder.append("#").append(i);
      stringBuilder.append("(pid ").append(paramMotionEvent.getPointerId(i));
      stringBuilder.append(")=").append((int)paramMotionEvent.getX(i));
      stringBuilder.append(",").append((int)paramMotionEvent.getY(i));
      if (i + 1 < paramMotionEvent.getPointerCount())
        stringBuilder.append(";"); 
    } 
    stringBuilder.append("]");
  }
  
  private String getContentText() {
    return this.mRenderer.getContentText();
  }
  
  public static void openIMEKeyboard() {
    Message message = new Message();
    message.what = 2;
    message.obj = mainView.getContentText();
    handler.sendMessage(message);
  }
  
  public void deleteBackward() {
    queueEvent(new Runnable() {
          public void run() {
            Cocos2dxGLSurfaceView.this.mRenderer.handleDeleteBackward();
          }
        });
  }
  
  public TextView getTextField() {
    return (TextView)this.mTextField;
  }
  
  protected void initView() {
    this.mRenderer = new Cocos2dxRenderer();
    setFocusableInTouchMode(true);
    setRenderer(this.mRenderer);
    textInputWraper = new TextInputWraper(this);
    handler = new Handler() {
        
        public void handleMessage(Message param1Message) {
          switch (param1Message.what) {
            default:
              return;
            case 2:
              if (Cocos2dxGLSurfaceView.this.mTextField != null && Cocos2dxGLSurfaceView.this.mTextField.requestFocus()) {
                Cocos2dxGLSurfaceView.this.mTextField.removeTextChangedListener(Cocos2dxGLSurfaceView.textInputWraper);
                Cocos2dxGLSurfaceView.this.mTextField.setText("");
                String str = (String)param1Message.obj;
                Cocos2dxGLSurfaceView.this.mTextField.append(str);
                Cocos2dxGLSurfaceView.textInputWraper.setOriginText(str);
                Cocos2dxGLSurfaceView.this.mTextField.addTextChangedListener(Cocos2dxGLSurfaceView.textInputWraper);
                ((InputMethodManager)Cocos2dxGLSurfaceView.mainView.getContext().getSystemService("input_method")).showSoftInput((View)Cocos2dxGLSurfaceView.this.mTextField, 0);
              } 
            case 3:
              break;
          } 
          if (Cocos2dxGLSurfaceView.this.mTextField != null) {
            Cocos2dxGLSurfaceView.this.mTextField.removeTextChangedListener(Cocos2dxGLSurfaceView.textInputWraper);
            ((InputMethodManager)Cocos2dxGLSurfaceView.mainView.getContext().getSystemService("input_method")).hideSoftInputFromWindow(Cocos2dxGLSurfaceView.this.mTextField.getWindowToken(), 0);
          } 
        }
      };
    mainView = this;
  }
  
  // USUNIĘTO final String val$text = text;
  public void insertText(final String text) {
    queueEvent(new Runnable() {
          public void run() {
            Cocos2dxGLSurfaceView.this.mRenderer.handleInsertText(text);
          }
        });
  }
  
  // USUNIĘTO final int val$kc = kc;
  public boolean onKeyDown(final int kc, KeyEvent paramKeyEvent) {
    if (kc == 4 || kc == 82) {
      queueEvent(new Runnable() {
            public void run() {
              Cocos2dxGLSurfaceView.this.mRenderer.handleKeyDown(kc);
            }
          });
      return true;
    } 
    return super.onKeyDown(kc, paramKeyEvent);
  }
  
  public void onPause() {
    queueEvent(new Runnable() {
          public void run() {
            Cocos2dxGLSurfaceView.this.mRenderer.handleOnPause();
          }
        });
    super.onPause();
  }
  
  public void onResume() {
    super.onResume();
    queueEvent(new Runnable() {
          public void run() {
            Cocos2dxGLSurfaceView.this.mRenderer.handleOnResume();
          }
        });
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.mRenderer.setScreenWidthAndHeight(paramInt1, paramInt2);
  }
  
  // NAPRAWIONA METODA onTouchEvent
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    int j = paramMotionEvent.getPointerCount();
    // Zmienne użyte jako final w klasach anonimowych MUSZĄ być deklarowane poza nimi
    // i nie mogą mieć prefiksu 'val$'. 
    final int[] ids = new int[j];
    final float[] xs = new float[j];
    final float[] ys = new float[j];
    final long eventTime = paramMotionEvent.getEventTime();
    int i;
    for (i = 0; i < j; i++) {
      ids[i] = paramMotionEvent.getPointerId(i);
      xs[i] = paramMotionEvent.getX(i);
      ys[i] = paramMotionEvent.getY(i);
    } 
    
    switch (paramMotionEvent.getAction() & 0xFF) {
      default:
        return true;

      case 5: // ACTION_POINTER_DOWN
        i = paramMotionEvent.getAction() >> 8;
        final int idPointerDown = paramMotionEvent.getPointerId(i);
        final float xPointerDown = paramMotionEvent.getX(i);
        final float yPointerDown = paramMotionEvent.getY(i);
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionDown(idPointerDown, xPointerDown, yPointerDown, eventTime);
              }
            });
        break;

      case 0: // ACTION_DOWN
        final int idDown = ids[0];
        final float xDown = xs[0];
        final float yDown = ys[0];
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionDown(idDown, xDown, yDown, eventTime);
              }
            });
        break;

      case 2: // ACTION_MOVE
        // W tym przypadku zmienne ids, xs, ys i eventTime są już finalne w zasięgu metody
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionMove(ids, xs, ys, eventTime);
              }
            });
        break;

      case 6: // ACTION_POINTER_UP
        i = paramMotionEvent.getAction() >> 8;
        final int idPointerUp = paramMotionEvent.getPointerId(i);
        final float xPointerUp = paramMotionEvent.getX(i);
        final float yPointerUp = paramMotionEvent.getY(i);
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionUp(idPointerUp, xPointerUp, yPointerUp, eventTime);
              }
            });
        break;

      case 1: // ACTION_UP
        final int idUp = ids[0];
        final float xUp = xs[0];
        final float yUp = ys[0];
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionUp(idUp, xUp, yUp, eventTime);
              }
            });
        break;

      case 3: // ACTION_CANCEL
        // W tym przypadku zmienne ids, xs, ys i eventTime są już finalne w zasięgu metody
        queueEvent(new Runnable() {
              // USUNIĘTO final long val$eventTime = eventTime; itd.
              public void run() {
                Cocos2dxGLSurfaceView.this.mRenderer.handleActionCancel(ids, xs, ys, eventTime);
              }
            });
        break;
    }
    return true; 
  }
  
  public void setTextField(Cocos2dxEditText paramCocos2dxEditText) {
    this.mTextField = paramCocos2dxEditText;
    if (this.mTextField != null && textInputWraper != null) {
      this.mTextField.setOnEditorActionListener(textInputWraper);
      this.mTextField.setMainView(this);
      requestFocus();
    } 
  }
}