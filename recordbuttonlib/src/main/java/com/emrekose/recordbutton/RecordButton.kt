package com.emrekose.recordbutton

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator

/**
 * Created by emrekose on 18.10.2017.
 */

class RecordButton : View, Animatable {

    /**
     * values to draw
     */
    private var buttonPaint: Paint? = null
    private var progressEmptyPaint: Paint? = null
    private var progressPaint: Paint? = null
    private var rectF: RectF? = null

    /**
     * Bitmap for record icon
     */
    private var bitmap: Bitmap? = null

    /**
     * record button radius
     */
    var buttonRadius: Float = 0.toFloat()
        private set

    /**
     * progress ring stroke
     */
    var progressStroke: Int = 0

    /**
     * spacing for button and progress ring
     */
    var buttonGap: Float = 0.toFloat()
        private set

    /**
     * record button fill color
     */
    var buttonColor: Int = 0

    /**
     * progress ring background color
     */
    var progressEmptyColor: Int = 0

    /**
     * progress ring arc color
     */
    var progressColor: Int = 0

    /**
     * record icon res id
     */
    var recordIcon: Int = 0

    private var isRecording = false

    private var currentMilliSecond = 0
    private var maxMilliSecond = 0

    /**
     * progress starting degress for arc
     */
    private val startAngle = 270

    /**
     * progress sweep angle degress for arc
     */
    private var sweepAngle: Int = 0

    /**
     * Listener that notify on record and record finish
     */
    private var recordListener: OnRecordListener? = null

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    /**
     * Initialize view
     * @param context
     * @param attrs
     */
    private fun init(context: Context, attrs: AttributeSet?) {
        val resource = context.obtainStyledAttributes(attrs, R.styleable.RecordButton)
        buttonRadius = resource.getDimension(R.styleable.RecordButton_buttonRadius, resources.displayMetrics.scaledDensity * 40f)
        progressStroke = resource.getInt(R.styleable.RecordButton_progressStroke, 10)
        buttonGap = resource.getDimension(R.styleable.RecordButton_buttonGap, resources.displayMetrics.scaledDensity * 8f)
        buttonColor = resource.getColor(R.styleable.RecordButton_buttonColor, Color.RED)
        progressEmptyColor = resource.getColor(R.styleable.RecordButton_progressEmptyColor, Color.LTGRAY)
        progressColor = resource.getColor(R.styleable.RecordButton_progressColor, Color.BLUE)
        recordIcon = resource.getResourceId(R.styleable.RecordButton_recordIcon, -1)
        maxMilliSecond = resource.getInt(R.styleable.RecordButton_maxMilisecond, 5000)
        resource.recycle()

        buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        buttonPaint?.color = buttonColor
        buttonPaint?.style = Paint.Style.FILL

        progressEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressEmptyPaint?.color = progressEmptyColor
        progressEmptyPaint?.style = Paint.Style.STROKE
        progressEmptyPaint?.strokeWidth = progressStroke.toFloat()

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint?.color = progressColor
        progressPaint?.style = Paint.Style.STROKE
        progressPaint?.strokeWidth = progressStroke.toFloat()
        progressPaint?.strokeCap = Paint.Cap.ROUND

        rectF = RectF()

        bitmap = BitmapFactory.decodeResource(context.resources, recordIcon)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cx = width / 2
        val cy = height / 2

        canvas.drawCircle(cx.toFloat(), cy.toFloat(), buttonRadius, buttonPaint!!)
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), buttonRadius + buttonGap, progressEmptyPaint!!)

        if (recordIcon != -1) {
            canvas.drawBitmap(bitmap!!, (cx - bitmap!!.height / 2).toFloat(), (cy - bitmap!!.width / 2).toFloat(), null)
        }

        sweepAngle = 360 * currentMilliSecond / maxMilliSecond
        rectF?.set(cx.toFloat() - buttonRadius - buttonGap, cy.toFloat() - buttonRadius - buttonGap, cx.toFloat() + buttonRadius + buttonGap, cy.toFloat() + buttonRadius + buttonGap)
        canvas.drawArc(rectF, startAngle.toFloat(), sweepAngle.toFloat(), false, progressPaint!!)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = buttonRadius.toInt() * 2 + buttonGap.toInt() * 2 + progressStroke + 30

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        width = when (widthMode) {
            View.MeasureSpec.EXACTLY -> widthSize
            View.MeasureSpec.AT_MOST -> Math.min(size, widthSize)
            View.MeasureSpec.UNSPECIFIED -> size
            else -> size
        }

        height = when (heightMode) {
            View.MeasureSpec.EXACTLY -> heightSize
            View.MeasureSpec.AT_MOST -> Math.min(size, heightSize)
            View.MeasureSpec.UNSPECIFIED -> size
            else -> size
        }

        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                start()
                progressAnimate().start()
                return true
            }
            MotionEvent.ACTION_UP -> {
                stop()
                return true
            }
        }
        return false
    }

    /**
     * record button scale animation starting
     */
    override fun start() {
        isRecording = true
        scaleAnimation(1.1f, 1.1f)
    }

    /**
     * record button scale animation stopping
     */
    override fun stop() {
        isRecording = false
        currentMilliSecond = 0

        scaleAnimation(1f, 1f)
    }

    override fun isRunning(): Boolean {
        return isRecording
    }

    /**
     * This method provides scale animation to view
     * between scaleX and scale Y values
     * @param scaleX
     * @param scaleY
     */
    private fun scaleAnimation(scaleX: Float, scaleY: Float) {
        this.animate().scaleX(scaleX).scaleY(scaleY).start()
    }

    /**
     * Progress starting animation
     * @return progress animate
     */
    @SuppressLint("ObjectAnimatorBinding")
    private fun progressAnimate(): ObjectAnimator {
        val animator = ObjectAnimator.ofInt(this, "progress", currentMilliSecond, maxMilliSecond)

        animator.addUpdateListener { animation ->
            val value = (animation.animatedValue as Float).toInt()

            if (isRecording) {
                setCurrentMilliSecond(value)
                if (recordListener != null) recordListener?.onRecord()
            } else {
                animation.cancel()
                isRecording = false
                if (recordListener != null) recordListener?.onRecordCancel()
            }

            if (value == maxMilliSecond) {
                if (recordListener != null) recordListener?.onRecordFinish()
                stop()
            }
        }

        animator.interpolator = LinearInterpolator()
        animator.duration = maxMilliSecond.toLong()
        return animator
    }

    private fun setCurrentMilliSecond(currentMilliSecond: Int) {
        this.currentMilliSecond = currentMilliSecond
        postInvalidate()
    }

    fun getCurrentMiliSecond(): Int {
        return currentMilliSecond
    }

    fun setButtonRadius(buttonRadius: Int) {
        this.buttonRadius = buttonRadius.toFloat()
    }

    fun setButtonGap(buttonGap: Int) {
        this.buttonGap = buttonGap.toFloat()
    }

    fun setRecordListener(recordListener: OnRecordListener) {
        this.recordListener = recordListener
    }


}
