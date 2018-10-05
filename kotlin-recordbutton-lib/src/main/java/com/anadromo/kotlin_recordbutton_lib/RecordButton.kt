package com.anadromo.kotlin_recordbutton_lib

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator

class RecordButton: View, Animatable {

    private val startAngle: Int = 270
    private var isRecording: Boolean = false
    private var sweepAngle: Int = 0
    lateinit var buttonPaint: Paint
    lateinit var progressEmptyPaint: Paint
    lateinit var progressPaint: Paint
    private lateinit var rectF: RectF
    lateinit var bitmap: Bitmap
    var buttonRadius: Float = 0f
    var progressStroke: Int = 0
    var buttonGap: Float = 0f
    var buttonColor: Int = 0
    var progressEmptyColor: Int = 0
    var progressColor: Int = 0
    var recordIcon: Int = 0
    var currentMiilisecond: Int = 0
        set(value) {
            field = value
            postInvalidate()
        }
    var maxMilisecond: Int = 0
    var recordListener: OnRecordListener? = null

    constructor(context: Context): super(context){
        init(context, null)
    }

    constructor(context: Context, attributeSet: AttributeSet?): super(context, attributeSet){
        init(context, attributeSet)
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int): super(context, attributeSet, defStyleAttr){
        init(context, attributeSet)
    }

    @SuppressLint("NewApi")
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes: Int): super(context, attributeSet, defStyleAttr,defStyleRes){
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet?){
        val a = context.obtainStyledAttributes(attributeSet, R.styleable.RecordButton)
        buttonRadius = a.getDimension(R.styleable.RecordButton_buttonRadius, resources.displayMetrics.scaledDensity * 40f)
        progressStroke = a.getInt(R.styleable.RecordButton_progressStroke, 10)
        buttonGap = a.getDimension(R.styleable.RecordButton_buttonGap, resources.displayMetrics.scaledDensity * 8f)
        buttonColor = a.getColor(R.styleable.RecordButton_buttonColor, Color.RED)
        progressEmptyColor = a.getColor(R.styleable.RecordButton_progressEmptyColor, Color.LTGRAY)
        progressColor = a.getColor(R.styleable.RecordButton_progressColor, Color.BLUE)
        recordIcon = a.getResourceId(R.styleable.RecordButton_recordIcon, -1)
        maxMilisecond = a.getInt(R.styleable.RecordButton_maxMilisecond, 5000)
        a.recycle()

        buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        buttonPaint.color = buttonColor
        buttonPaint.style = Paint.Style.FILL

        progressEmptyPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressEmptyPaint.color = progressEmptyColor
        progressEmptyPaint.style = Paint.Style.STROKE
        progressEmptyPaint.strokeWidth = progressStroke.toFloat()

        progressPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressPaint.color = progressColor
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = progressStroke.toFloat()
        progressPaint.strokeCap = Paint.Cap.ROUND

        rectF = RectF()

        bitmap = BitmapFactory.decodeResource(context.resources, recordIcon)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(canvas is Canvas){
            val cx: Float = (width / 2).toFloat()
            val cy: Float = (height / 2).toFloat()

            canvas.drawCircle(cx, cy, buttonRadius, buttonPaint)
            canvas.drawCircle(cx, cy, buttonRadius + buttonGap, progressEmptyPaint)

            if(recordIcon != -1) canvas.drawBitmap(bitmap, cx - bitmap.height / 2, cy - bitmap.width / 2, null)

            sweepAngle = 360 * currentMiilisecond / maxMilisecond

            rectF.set(cx - buttonRadius - buttonGap, cy - buttonRadius - buttonGap, cx + buttonRadius + buttonGap, cy + buttonRadius + buttonGap)
            canvas.drawArc(rectF, startAngle.toFloat(), sweepAngle.toFloat(), false, progressPaint)
        }
    }

    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size: Int = buttonRadius.toInt() * 2 + buttonGap.toInt() * 2 + progressStroke + 30

        val widthMode: Int = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize: Int = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode: Int = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize: Int = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when(widthMode){
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(size, widthSize)
            else -> size
        }

        val height: Int = when(heightMode){
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(size, heightSize)
            else -> size
        }

        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event is MotionEvent){
            return when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    start()
                    progressAnimate().start()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    stop()
                    true
                }
                else -> false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun isRunning(): Boolean = this.isRecording

    override fun start() {
        isRecording = true
        scaleAnimation(1.1f, 1.1f)
    }

    override fun stop() {
        isRecording = false
        currentMiilisecond = 0
        scaleAnimation(1f,1f)
    }

    private fun scaleAnimation(scaleX: Float, scaleY: Float){
        this.animate().scaleX(scaleX).scaleY(scaleY).start()
    }

    private fun progressAnimate(): ObjectAnimator {
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(this, "progress", currentMiilisecond.toFloat(), maxMilisecond.toFloat())
        animator.addUpdateListener{animation ->
            val value = animation.animatedValue as Int

            if(isRecording){
                currentMiilisecond = value
                if(recordListener != null) recordListener?.onRecord()
            }else{
                animation.cancel()
                isRecording = false
                if(recordListener != null) recordListener?.onRecordCancel()
            }

            if(value == maxMilisecond){
                if(recordListener != null) recordListener?.onRecordFinish()
                stop()
            }
        }

        animator.interpolator = LinearInterpolator()
        animator.duration = maxMilisecond.toLong()
        return animator
    }
}