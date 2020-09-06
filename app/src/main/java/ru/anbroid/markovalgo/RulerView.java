package ru.anbroid.markovalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

public class RulerView extends View
{
    private Paint paint;
    private Rect mTextBoundRect;
    private int width;
    private int height;
    private int step;
    private int strokeWidth;

    public RulerView(Context context, AttributeSet foo)
    {
        super(context, foo);

        paint = new Paint();
        step = 100;
        strokeWidth = 5;
        mTextBoundRect = new Rect();

        setBackgroundColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(false);
        paint.setColor(Color.BLACK);
        paint.setTextSize(25);
    }

    public void onSizeChanged(int w, int h, int oldW, int oldH)
    {
        width = w;
        height = h;
    }

    public void onDraw(Canvas c)
    {
        float size;
        String text;

        int left = getPaddingLeft();
        int right = getPaddingRight();

        c.drawLine(left, height - 3, width - right, height - 3, paint);

        for (int i = left; i < width - right; i++)
        {
            if (i % step == 0)
            {
                size = Math.round(height / 2f);
                text = Integer.toString(i / 10);
                paint.getTextBounds(text, 0, text.length(), mTextBoundRect);

                float textWidth = paint.measureText(text);
                float textHeight = mTextBoundRect.height();
                paint.setAntiAlias(true);
                c.drawText(text, i - textWidth / 2f,size - textHeight / 2f, paint);
                paint.setAntiAlias(false);
                c.drawLine(i, height, i, size, paint);
            }
            else if (i % 50 == 0)
            {
                size = height - left;
                c.drawLine(i, height, i, size, paint);
            }
        }

        super.onDraw(c);
    }
}