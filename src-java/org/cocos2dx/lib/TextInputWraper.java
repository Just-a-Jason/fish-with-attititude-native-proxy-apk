package org.cocos2dx.lib;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

class TextInputWraper implements TextWatcher, TextView.OnEditorActionListener {

    private static final Boolean debug = Boolean.valueOf(false);

    private Cocos2dxGLSurfaceView mMainView;

    private String mOriginText;

    private String mText;

    public TextInputWraper(Cocos2dxGLSurfaceView paramCocos2dxGLSurfaceView) {
        this.mMainView = paramCocos2dxGLSurfaceView;
    }

    private void LogD(String paramString) {
        if (debug.booleanValue()) Log.d("TextInputFilter", paramString);
    }

    private Boolean isFullScreenEdit() {
        return Boolean.valueOf(
            (
                (InputMethodManager) this.mMainView
                    .getTextField()
                    .getContext()
                    .getSystemService("input_method")
            ).isFullscreenMode()
        );
    }

    public void afterTextChanged(Editable paramEditable) {
        if (isFullScreenEdit().booleanValue()) return;
        LogD("afterTextChanged: " + paramEditable);
        int j = paramEditable.length() - this.mText.length();
        int i = j;
        if (j > 0) {
            String str = paramEditable
                .subSequence(this.mText.length(), paramEditable.length())
                .toString();
            this.mMainView.insertText(str);
            LogD("insertText(" + str + ")");
        } else {
            while (true) {
                if (i < 0) {
                    this.mMainView.deleteBackward();
                    LogD("deleteBackward");
                    i++;
                    continue;
                }
                this.mText = paramEditable.toString();
                return;
            }
        }
        this.mText = paramEditable.toString();
    }

    public void beforeTextChanged(
        CharSequence paramCharSequence,
        int paramInt1,
        int paramInt2,
        int paramInt3
    ) {
        LogD(
            "beforeTextChanged(" +
                paramCharSequence +
                ")start: " +
                paramInt1 +
                ",count: " +
                paramInt2 +
                ",after: " +
                paramInt3
        );
        this.mText = paramCharSequence.toString();
    }

    public boolean onEditorAction(
        TextView paramTextView,
        int paramInt,
        KeyEvent paramKeyEvent
    ) {
        if (
            this.mMainView.getTextField() == paramTextView &&
            isFullScreenEdit().booleanValue()
        ) {
            for (
                paramInt = this.mOriginText.length();
                paramInt > 0;
                paramInt--
            ) {
                this.mMainView.deleteBackward();
                LogD("deleteBackward");
            }
            String str2 = paramTextView.getText().toString();
            String str1 = str2;
            if (str2.compareTo("") == 0) str1 = "\n";
            str2 = str1;
            if ('\n' != str1.charAt(str1.length() - 1)) str2 = str1 + '\n';
            this.mMainView.insertText(str2);
            LogD("insertText(" + str2 + ")");
        }
        return false;
    }

    public void onTextChanged(
        CharSequence paramCharSequence,
        int paramInt1,
        int paramInt2,
        int paramInt3
    ) {}

    public void setOriginText(String paramString) {
        this.mOriginText = paramString;
    }
}
