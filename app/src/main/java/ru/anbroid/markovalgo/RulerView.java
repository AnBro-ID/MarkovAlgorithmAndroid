package ru.anbroid.markovalgo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class RulerView extends View
{
    private Paint paint;
    private Rect mTextBoundRect;
    private int width;
    private int height;
    private int step;
    private int strokeWidth;

    public RulerView(Context context, AttributeSet atrset)
    {
        super(context, atrset);

        paint = new Paint();
        step = 100;
        strokeWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mTextBoundRect = new Rect();

        setBackgroundColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(false);
        paint.setColor(Color.BLACK);
        paint.setTextSize(Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics())));
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

        c.drawLine(left, height - 1, width - right, height - 1, paint);

        for (int i = left; i < width - right; i++)
        {
            if (i % step == 0)
            {
                size = Math.round(height / 1.5f);
                text = Integer.toString(i / 10);
                paint.getTextBounds(text, 0, text.length(), mTextBoundRect);

                float textWidth = paint.measureText(text);
                float textHeight = mTextBoundRect.height();

                paint.setAntiAlias(true);
                c.drawText(text, i - textWidth / 2f,size - textHeight / 2f, paint);
                paint.setAntiAlias(false);

                c.drawLine(i, height, i, size, paint);
            }
            else if (i % (step / 2) == 0)
            {
                size = height - left / 2f;
                c.drawLine(i, height, i, size, paint);
            }
        }

        super.onDraw(c);
    }
}