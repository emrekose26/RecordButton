package com.emrekose.recordbutton;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by emrekose on 18.10.2017.
 */

public class RecordButton extends View implements Animatable {

    /**
     * values to draw
     */
    private Paint buttonPaint, progressEmptyPaint, progressPaint;
    private RectF rectF;

    /**
     * Bitmap for record icon
     */
    private Bitmap bitmap;

    /**
     * record button radius
     */
    private float buttonRadius;

    /**
     * progress ring stroke
     */
    private int progressStroke;

    /**
     * spacing for button and progress ring
     */
    private float buttonGap;

    /**
     * record button fill color
     */
    private int buttonColor;

    /**
     * progress ring background color
     */
    private int progressEmptyColor;

    /**
     * progress ring arc color
     */
    private int progressColor;

    /**
     * record icon res id
     */
    private int recordIcon;

    private boolean isRecording = false;

    private int currentMiliSecond = 0;
    private int maxMilisecond;

    /**
     * progress starting degress for arc
     */
    private int startAngle = 270;

    /**
     * progress sweep angle degress for arc
     */
    private int sweepAngle;

    /**
     * Listener that notify on record and record finish
     */
    OnRecordListener recordListener;

    public RecordButton(Context context) {
        super(context);
        init(context, null);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @SuppressLint("NewApi")
    public RecordButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initialize view
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecordButton);
        buttonRadius = a.getDimension(R.styleable.RecordButton_buttonRadius, getResources().getDisplayMetrics().scaledDensity * 40f);
        progressStroke = a.getInt(R.styleable.RecordButton_progressStroke, 10);
        buttonGap = a.getDimension(R.styleable.RecordButton_buttonGap, getResources().getDisplayMetrics().scaledDensity * 8f);
        buttonColor = a.getColor(R.styleable.RecordButton_buttonColor, Color.RED);
        progressEmptyColor = a.getColor(R.styleable.RecordButton_progressEmptyColor, Color.LTGRAY);
        progressColor = a.getColor(R.styleable.RecordButton_progressColor, Color.BLUE);
        recordIcon = a.getResourceId(R.styleable.RecordButton_recordIcon, -1);
        maxMilisecond = a.getInt(R.styleable.RecordButton_maxMilisecond, 5000);
        a.recycle();

        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(buttonColor);
        buttonPaint.setStyle(Paint.Style.FILL);

        progressEmptyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressEmptyPaint.setColor(progressEmptyColor);
        progressEmptyPaint.setStyle(Paint.Style.STROKE);
        progressEmptyPaint.setStrokeWidth(progressStroke);

        progressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        progressPaint.setColor(progressColor);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressStroke);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);

        rectF = new RectF();

        bitmap = BitmapFactory.decodeResource(context.getResources(), recordIcon);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int cx = getWidth() / 2;
        int cy = getHeight() / 2;

        canvas.drawCircle(cx, cy, buttonRadius, buttonPaint);
        canvas.drawCircle(cx, cy, buttonRadius + buttonGap, progressEmptyPaint);

        if (recordIcon != -1) {
            canvas.drawBitmap(bitmap, cx - bitmap.getHeight() / 2, cy - bitmap.getWidth() / 2, null);
        }

        sweepAngle = 360 * currentMiliSecond / maxMilisecond;
        rectF.set(cx - buttonRadius - buttonGap, cy - buttonRadius - buttonGap, cx + buttonRadius + buttonGap, cy + buttonRadius + buttonGap);
        canvas.drawArc(rectF, startAngle, sweepAngle, false, progressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = ((int) buttonRadius * 2 + (int) buttonGap * 2 + progressStroke + 30);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(size, widthSize);
        } else {
            width = size;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(size, heightSize);
        } else {
            height = size;
        }

        setMeasuredDimension(width, height);
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                start();
                progressAnimate().start();
                return true;
            case MotionEvent.ACTION_UP:
                stop();
                return true;
        }
        return false;
    }

    /**
     * record button scale animation starting
     */
    @Override
    public void start() {
        isRecording = true;
        scaleAnimation(1.1f, 1.1f);
    }

    /**
     * record button scale animation stopping
     */
    @Override
    public void stop() {
        isRecording = false;
        currentMiliSecond = 0;

        scaleAnimation(1f, 1f);
    }

    @Override
    public boolean isRunning() {
        return isRecording;
    }

    /**
     * This method provides scale animation to view
     * between scaleX and scale Y values
     * @param scaleX
     * @param scaleY
     */
    private void scaleAnimation(float scaleX, float scaleY) {
        this.animate().scaleX(scaleX).scaleY(scaleY).start();
    }

    /**
     *  Progress starting animation
     * @return progress animate
     */
    private ObjectAnimator progressAnimate() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", currentMiliSecond, maxMilisecond);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = ((Float) (animation.getAnimatedValue())).intValue();

                if (isRecording) {
                    setCurrentMiliSecond(value);
                    if (recordListener != null) recordListener.onRecord();
                } else {
                    animation.cancel();
                    isRecording = false;
                    if (recordListener != null) recordListener.onRecordCancel();
                }

                if (value == maxMilisecond) {
                    if (recordListener != null) recordListener.onRecordFinish();
                    stop();
                }
            }
        });

        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(maxMilisecond);
        return animator;

    }

    public void setCurrentMiliSecond(int currentMiliSecond) {
        this.currentMiliSecond = currentMiliSecond;
        postInvalidate();
    }

    public int getCurrentMiliSecond() {
        return currentMiliSecond;
    }

    public Paint getButtonPaint() {
        return buttonPaint;
    }

    public void setButtonPaint(Paint buttonPaint) {
        this.buttonPaint = buttonPaint;
    }

    public Paint getProgressEmptyPaint() {
        return progressEmptyPaint;
    }

    public void setProgressEmptyPaint(Paint progressEmptyPaint) {
        this.progressEmptyPaint = progressEmptyPaint;
    }

    public Paint getProgressPaint() {
        return progressPaint;
    }

    public void setProgressPaint(Paint progressPaint) {
        this.progressPaint = progressPaint;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getButtonRadius() {
        return buttonRadius;
    }

    public void setButtonRadius(int buttonRadius) {
        this.buttonRadius = buttonRadius;
    }

    public int getProgressStroke() {
        return progressStroke;
    }

    public void setProgressStroke(int progressStroke) {
        this.progressStroke = progressStroke;
    }

    public float getButtonGap() {
        return buttonGap;
    }

    public void setButtonGap(int buttonGap) {
        this.buttonGap = buttonGap;
    }

    public int getButtonColor() {
        return buttonColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public int getProgressEmptyColor() {
        return progressEmptyColor;
    }

    public void setProgressEmptyColor(int progressEmptyColor) {
        this.progressEmptyColor = progressEmptyColor;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getMaxMilisecond() {
        return maxMilisecond;
    }

    public void setMaxMilisecond(int maxMilisecond) {
        this.maxMilisecond = maxMilisecond;
    }

    public int getRecordIcon() {
        return recordIcon;
    }

    public void setRecordIcon(int recordIcon) {
        this.recordIcon = recordIcon;
    }

    public void setRecordListener(final OnRecordListener recordListener) {
        this.recordListener = recordListener;
    }


}
