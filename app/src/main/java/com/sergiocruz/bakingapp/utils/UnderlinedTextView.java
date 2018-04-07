package com.sergiocruz.bakingapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.sergiocruz.bakingapp.R;

public class UnderlinedTextView extends android.support.v7.widget.AppCompatTextView {

    private Paint paint;

    public UnderlinedTextView(Context context) {
        super(context);
        init();
    }

    public UnderlinedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UnderlinedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.brown500));
        paint.setStrokeWidth(getLineHeight() / 10);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float startX = getPaddingLeft();
        float stopX = getWidth() - getPaddingRight();
        float offsetY = getPaddingTop() - getPaint().getFontMetrics().top + paint.getStrokeWidth() / 2;

        for (int i = 0; i < getLineCount(); ++i) {
            float y = offsetY + getLineHeight() * i;
            canvas.drawLine(startX, y, stopX, y, paint);
        }

        super.onDraw(canvas);
    }

}
