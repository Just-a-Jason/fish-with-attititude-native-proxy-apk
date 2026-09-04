package org.cocos2dx.lib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.FloatMath;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;

public class Cocos2dxBitmap {

    private static final String TAG = "Cocos2dxBitmap";

    private static final int HORIZONTALALIGN_CENTER = 3;

    private static final int HORIZONTALALIGN_LEFT = 1;

    private static final int HORIZONTALALIGN_RIGHT = 2;

    private static final int VERTICALALIGN_BOTTOM = 2;

    private static final int VERTICALALIGN_CENTER = 3;

    private static final int VERTICALALIGN_TOP = 1;

    private static Context sContext;

    private static TextProperty computeTextProperty(
        String paramString,
        int paramInt1,
        int paramInt2,
        Paint paramPaint
    ) {
        Paint.FontMetricsInt fontMetricsInt = paramPaint.getFontMetricsInt();
        int j = (int) Math.ceil(fontMetricsInt.bottom - fontMetricsInt.top);
        int i = 0;
        String[] arrayOfString = splitString(
            paramString,
            paramInt1,
            paramInt2,
            paramPaint
        );
        if (paramInt1 != 0) {
            i = paramInt1;
            return new TextProperty(i, j, arrayOfString);
        }
        int k = arrayOfString.length;
        paramInt2 = 0;
        paramInt1 = i;
        while (true) {
            i = paramInt1;
            if (paramInt2 < k) {
                paramString = arrayOfString[paramInt2];
                int m = (int) FloatMath.ceil(
                    paramPaint.measureText(paramString, 0, paramString.length())
                );
                i = paramInt1;
                if (m > paramInt1) i = m;
                paramInt2++;
                paramInt1 = i;
                continue;
            }
            return new TextProperty(i, j, arrayOfString);
        }
    }

    private static int computeX(
        String paramString,
        int paramInt1,
        int paramInt2
    ) {
        int xOffset = 0; // Zastąpiono 'bool' i zadeklarowano xOffset
        switch (paramInt2) {
            default:
                paramInt1 = xOffset;
                break;
            case 3: // Center
                paramInt1 /= 2;
                break;
            case 2: // Right
                break;
        }
        return paramInt1;
    }

    // --- POPRAWKA 2 & 3: Naprawa zmiennej 'null' i typu zwracanego ---
    private static int computeY(
        Paint.FontMetricsInt paramFontMetricsInt,
        int paramInt1,
        int paramInt2,
        int paramInt3
    ) {
        int i = -paramFontMetricsInt.top;
        int tempY = i; // Zastąpiono 'null' zmienną 'tempY'
        if (paramInt1 > paramInt2) {
            switch (paramInt3) {
                default:
                    return i;
                case 1:
                    return -paramFontMetricsInt.top;
                case 3:
                    return (
                        -paramFontMetricsInt.top + (paramInt1 - paramInt2) / 2
                    );
                case 2:
                    break;
            }
        } else {
            return tempY; // Poprawny zwrot int, nie dosłowne null
        }
        return -paramFontMetricsInt.top + paramInt1 - paramInt2;
    }

    public static void createTextBitmap(
        String paramString1,
        String paramString2,
        int paramInt1,
        int paramInt2,
        int paramInt3,
        int paramInt4
    ) {
        int i = paramInt2 & 0xF;
        String str = refactorString(paramString1);
        Paint paint = newPaint(paramString2, paramInt1, i);
        TextProperty textProperty = computeTextProperty(
            str,
            paramInt3,
            paramInt4,
            paint
        );
        if (paramInt4 == 0) {
            paramInt1 = textProperty.mTotalHeight;
        } else {
            paramInt1 = paramInt4;
        }
        Bitmap bitmap = Bitmap.createBitmap(
            textProperty.mMaxWidth,
            paramInt1,
            Bitmap.Config.ARGB_8888
        );
        Canvas canvas = new Canvas(bitmap);
        paramInt2 = computeY(
            paint.getFontMetricsInt(),
            paramInt4,
            textProperty.mTotalHeight,
            (paramInt2 >> 4) & 0xF
        );
        String[] arrayOfString = textProperty.mLines;
        paramInt3 = arrayOfString.length;
        for (paramInt1 = 0; paramInt1 < paramInt3; paramInt1++) {
            paramString2 = arrayOfString[paramInt1];
            canvas.drawText(
                paramString2,
                computeX(paramString2, textProperty.mMaxWidth, i),
                paramInt2,
                paint
            );
            paramInt2 += textProperty.mHeightPerLine;
        }
        initNativeObject(bitmap);
    }

    private static LinkedList<String> divideStringWithMaxWidth(
        String paramString,
        int paramInt,
        Paint paramPaint
    ) {
        int k = paramString.length();
        int j = 0;
        LinkedList<String> linkedList = new LinkedList();
        int i = 1;
        while (i <= k) {
            int i1 = (int) FloatMath.ceil(
                paramPaint.measureText(paramString, j, i)
            );
            int n = i;
            int m = j;
            if (i1 >= paramInt) {
                m = paramString.substring(0, i).lastIndexOf(" ");
                if (m != -1 && m > j) {
                    linkedList.add(paramString.substring(j, m));
                    i = m;
                } else if (i1 > paramInt) {
                    linkedList.add(paramString.substring(j, i - 1));
                    i--;
                } else {
                    linkedList.add(paramString.substring(j, i));
                }
                while (true) {
                    j = i + 1;
                    if (paramString.indexOf(i) == 32) {
                        i = j;
                        continue;
                    }
                    m = j;
                    n = j;
                    break;
                }
            }
            i = n + 1;
            j = m;
        }
        if (j < k) linkedList.add(paramString.substring(j));
        return linkedList;
    }

    // --- POPRAWKA 1: Naprawa logiki pętli (usunięcie boolean rzutowanego na int) ---
    private static int getFontSizeAccordingHeight(int paramInt) {
        Paint paint = new Paint();
        Rect rect = new Rect();
        paint.setTypeface(Typeface.DEFAULT);
        int i = 1;

        while (true) {
            // Zmieniono pętlę, aby zrezygnować z rzutowania boolean/int
            paint.setTextSize(i);
            paint.getTextBounds("SghMNy", 0, "SghMNy".length(), rect);

            if (paramInt - rect.height() <= 2) {
                return i;
            }
            i++;
        }
    }

    private static byte[] getPixels(Bitmap paramBitmap) {
        if (paramBitmap != null) {
            byte[] arrayOfByte = new byte[paramBitmap.getWidth() *
                paramBitmap.getHeight() *
                4];
            ByteBuffer byteBuffer = ByteBuffer.wrap(arrayOfByte);
            byteBuffer.order(ByteOrder.nativeOrder());
            paramBitmap.copyPixelsToBuffer(byteBuffer);
            return arrayOfByte;
        }
        return null;
    }

    private static String getStringWithEllipsis(
        String paramString,
        float paramFloat1,
        float paramFloat2
    ) {
        if (TextUtils.isEmpty(paramString)) return "";
        TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextSize(paramFloat2);
        return TextUtils.ellipsize(
            paramString,
            textPaint,
            paramFloat1,
            TextUtils.TruncateAt.END
        ).toString();
    }

    private static void initNativeObject(Bitmap paramBitmap) {
        byte[] arrayOfByte = getPixels(paramBitmap);
        if (arrayOfByte != null) nativeInitBitmapDC(
            paramBitmap.getWidth(),
            paramBitmap.getHeight(),
            arrayOfByte
        );
    }

    private static native void nativeInitBitmapDC(
        int paramInt1,
        int paramInt2,
        byte[] paramArrayOfbyte
    );

    private static Paint newPaint(
        String paramString,
        int paramInt1,
        int paramInt2
    ) {
        Paint paint = new Paint();
        paint.setColor(-1);
        paint.setTextSize(paramInt1);
        paint.setAntiAlias(true);
        paramString = paramString + ".ttf";
        if (paramString.endsWith(".ttf")) {
            try {
                paint.setTypeface(Cocos2dxTypefaces.get(sContext, paramString));
                switch (paramInt2) {
                    default:
                        paint.setTextAlign(Paint.Align.LEFT);
                        return paint;
                    case 3:
                        paint.setTextAlign(Paint.Align.CENTER);
                        return paint;
                    case 2:
                        break;
                }
            } catch (Exception exception) {
                paint.setTypeface(Typeface.create(paramString, 0));
                switch (paramInt2) {
                    default:
                        paint.setTextAlign(Paint.Align.LEFT);
                        return paint;
                    case 3:
                        paint.setTextAlign(Paint.Align.CENTER);
                        return paint;
                    case 2:
                        break;
                }
            }
        } else {
            paint.setTypeface(Typeface.create(paramString, 0));
            switch (paramInt2) {
                default:
                    paint.setTextAlign(Paint.Align.LEFT);
                    return paint;
                case 3:
                    paint.setTextAlign(Paint.Align.CENTER);
                    return paint;
                case 2:
                    break;
            }
        }
        paint.setTextAlign(Paint.Align.RIGHT);
        return paint;
    }

    private static String refactorString(String paramString) {
        if (paramString.compareTo("") == 0) return " ";
        StringBuilder stringBuilder = new StringBuilder(paramString);
        int i = 0;
        int j = stringBuilder.indexOf("\n");
        while (true) {
            if (j != -1) {
                if (j == 0 || stringBuilder.charAt(j - 1) == '\n') {
                    stringBuilder.insert(i, " ");
                    i = j + 2;
                } else {
                    i = j + 1;
                }
                if (
                    i <= stringBuilder.length() && j != stringBuilder.length()
                ) {
                    j = stringBuilder.indexOf("\n", i);
                    continue;
                }
            }
            return stringBuilder.toString();
        }
    }

    public static void setContext(Context paramContext) {
        sContext = paramContext;
    }

    private static String[] splitString(
        String paramString,
        int paramInt1,
        int paramInt2,
        Paint paramPaint
    ) {
        String[] arrayOfString = paramString.split("\\n");
        Paint.FontMetricsInt fontMetricsInt = paramPaint.getFontMetricsInt();
        int i =
            paramInt2 /
            (int) Math.ceil(fontMetricsInt.bottom - fontMetricsInt.top);
        if (paramInt1 != 0) {
            LinkedList<String> linkedList = new LinkedList();
            int j = arrayOfString.length;
            paramInt2 = 0;
            while (true) {
                if (paramInt2 < j) {
                    String str = arrayOfString[paramInt2];
                    if (
                        (int) FloatMath.ceil(paramPaint.measureText(str)) >
                        paramInt1
                    ) {
                        linkedList.addAll(
                            divideStringWithMaxWidth(str, paramInt1, paramPaint)
                        );
                    } else {
                        linkedList.add(str);
                    }
                    if (i <= 0 || linkedList.size() < i) {
                        paramInt2++;
                        continue;
                    }
                }
                if (i > 0 && linkedList.size() > i) while (
                        linkedList.size() > i
                    )
                    linkedList.removeLast();
                break;
            }
            arrayOfString = new String[linkedList.size()];
            linkedList.toArray(arrayOfString);
            return arrayOfString;
        }
        if (paramInt2 != 0 && arrayOfString.length > i) {
            LinkedList<String> linkedList = new LinkedList();
            for (paramInt1 = 0; paramInt1 < i; paramInt1++) linkedList.add(
                arrayOfString[paramInt1]
            );
            arrayOfString = new String[linkedList.size()];
            linkedList.toArray(arrayOfString);
        }
        return arrayOfString;
    }

    private static class TextProperty {

        private final int mHeightPerLine;

        private final String[] mLines;

        private final int mMaxWidth;

        private final int mTotalHeight;

        TextProperty(
            int param1Int1,
            int param1Int2,
            String[] param1ArrayOfString
        ) {
            this.mMaxWidth = param1Int1;
            this.mHeightPerLine = param1Int2;
            this.mTotalHeight = param1ArrayOfString.length * param1Int2;
            this.mLines = param1ArrayOfString;
        }
    }
}
