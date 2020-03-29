package com.example.myapplication.circleslider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.myapplication.R;

import java.util.Arrays;

public class CircleSelector extends View {

    public interface OnSelectorListener {
        void onSelectorMove(int _value);
    }

    private int thumbX;
    private int thumbY;

    private int circleCenterX;
    private int circleCenterY;
    private int circleRadius;

    private Drawable thumbImage;
    private int padding;
    private int thumbSize;
    private int borderColor;
    private int[] borderGradientColors;
    private int borderThickness;
    private double startAngle;
    private double angle = startAngle;
    private boolean isThumbSelected = false;

    private Paint paint = new Paint();
    private SweepGradient gradientShader;
    private OnSelectorListener listener;

    public CircleSelector(Context context) {
        this(context, null);
    }

    public CircleSelector(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleSelector(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleSelector, defStyleAttr, 0);

        float startAngle = (float) Math.PI / 2;
        float angle = (float) Math.PI / 2;
        int thumbSize = typedArray.getDimensionPixelSize(R.styleable.CircleSelector_thumb_size, 50);
        int borderThickness = typedArray.getDimensionPixelSize(R.styleable.CircleSelector_border_thickness, 20);
        int borderColor = typedArray.getColor(R.styleable.CircleSelector_border_color, Color.RED);
        String borderGradientColors = typedArray.getString(R.styleable.CircleSelector_border_gradient_colors);
        Drawable thumbImage = typedArray.getDrawable(R.styleable.CircleSelector_thumb_image);
        int maxValue = typedArray.getInt(R.styleable.CircleSelector_max_value, 1000);

        setMaxValue(maxValue);
        setStartAngle(startAngle);
        setAngle(angle);
        setBorderThickness(borderThickness);
        setBorderColor(borderColor);
        if (null != borderGradientColors) {
            setBorderGradientColors(borderGradientColors.split(";"));
        }
        setThumbSize(thumbSize);
        setThumbImage(thumbImage);

        int padding = (getPaddingStart() + getPaddingEnd() + getPaddingBottom() + getPaddingTop()) / 4;
        setPadding(padding);

        typedArray.recycle();
    }

    public void setStartAngle(double _startAngle) {
        startAngle = _startAngle;
    }

    public void setAngle(double _angle) {
        angle = _angle;
    }

    public void setThumbSize(int _thumbSize) {
        thumbSize = _thumbSize;
    }

    public void setBorderThickness(int circleBorderThickness) {
        borderThickness = circleBorderThickness;
    }

    public void setBorderColor(int color) {
        borderColor = color;
    }

    public void setBorderGradientColors(String[] colors) {
        borderGradientColors = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            borderGradientColors[i] = Color.parseColor(colors[i]);
        }
    }

    @SuppressWarnings("unused")
    public void setBorderGradientColors(int[] colors) {
        if (null == colors) {
            borderGradientColors = null;
            gradientShader = null;
        } else {
            borderGradientColors = Arrays.copyOf(colors, colors.length);
            gradientShader = new SweepGradient(circleRadius, circleRadius, borderGradientColors, null);
        }
    }

    public void setThumbImage(Drawable drawable) {
        thumbImage = drawable;
    }

    public void setPadding(int _padding) {
        padding = _padding;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        int smallerDim = Math.min(w, h);

        int largestCenteredSquareLeft = (w - smallerDim) / 2;
        int largestCenteredSquareTop = (h - smallerDim) / 2;
        int largestCenteredSquareRight = largestCenteredSquareLeft + smallerDim;
        int largestCenteredSquareBottom = largestCenteredSquareTop + smallerDim;

        circleCenterX = largestCenteredSquareRight / 2 + (w - largestCenteredSquareRight) / 2;
        circleCenterY = largestCenteredSquareBottom / 2 + (h - largestCenteredSquareBottom) / 2;
        circleRadius = smallerDim / 2 - borderThickness / 2 - padding;

        if (null != borderGradientColors) {
            gradientShader = new SweepGradient(circleRadius, circleRadius, borderGradientColors, null);
        }

        super.onSizeChanged(w, h, oldW, oldH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(borderColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderThickness);
        paint.setAntiAlias(true);
        if (null != gradientShader) {
            paint.setShader(gradientShader);
        }
        canvas.drawCircle(circleCenterX, circleCenterY, circleRadius, paint);

        thumbX = (int) (circleCenterX + circleRadius * Math.cos(angle));
        thumbY = (int) (circleCenterY - circleRadius * Math.sin(angle));

        if (null != thumbImage) {
            thumbImage.setBounds(thumbX - thumbSize / 2, thumbY - thumbSize / 2, thumbX + thumbSize / 2, thumbY + thumbSize / 2);
            thumbImage.draw(canvas);
        } else {
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(thumbX, thumbY, thumbSize, paint);
        }
    }

    private void updateSliderState(int touchX, int touchY) {
        int distanceX = touchX - circleCenterX;
        int distanceY = circleCenterY - touchY;

        double c = Math.sqrt(Math.pow(distanceX, 2) + Math.pow(distanceY, 2));
        angle = Math.acos(distanceX / c);
        if (distanceY < 0) {
            angle = -angle;
        }

        double tAngle = Math.toDegrees((Math.PI/2) - angle);
        if (0 > tAngle) tAngle = 360d + tAngle;
        value = (int)((double)getMaxValue() * tAngle / 360d);

        if (null != listener) {
            listener.onSelectorMove(value);
        }
    }

    private int value;

    public void setValue(int _value) {
        int w = getWidth()/2;
        int h = getHeight()/2;

        double theta = (360d * (double)_value / (double)getMaxValue()) - 90d;
        double cosTheta = Math.cos(Math.toRadians(theta));
        double sinTheta = Math.sin(Math.toRadians(theta));

        int pX = w + (int) (w * cosTheta);
        int py = h + (int) (w * sinTheta);

        updateSliderState(pX, py);
        invalidate();
    }

    public int getValue() {
        return value;
    }

    private int maxValue = -1;

    public void setMaxValue(int _maxValue) {
        if (0>=_maxValue) maxValue = 1000;
        else maxValue = _maxValue;
    }

    public int getMaxValue() {
        if (0>=maxValue) {
            maxValue = 1000;
        }
        return maxValue;
    }

    public void setOnSelectorListener(OnSelectorListener _listener) {
        listener = _listener;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                int x = (int) ev.getX();
                int y = (int) ev.getY();
                if (x < thumbX + thumbSize && x > thumbX - thumbSize && y < thumbY + thumbSize && y > thumbY - thumbSize) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    isThumbSelected = true;
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (isThumbSelected) {
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    updateSliderState(x, y);
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                getParent().requestDisallowInterceptTouchEvent(false);
                isThumbSelected = false;
                break;
            }
        }

        invalidate();
        return true;
    }

}
