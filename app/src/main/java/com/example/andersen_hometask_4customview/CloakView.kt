package com.example.andersen_hometask_4customview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnRepeat

class CloaksView @JvmOverloads constructor(context: Context, attrs: AttributeSet) :
    View(context, attrs), ValueAnimator.AnimatorUpdateListener {

    companion object {
        private val ANIMATION_DURATION = 60000L
        private const val OUT_CIRCLE_RADIUS = 600f

        private const val DEFAULT_SECOND_LINE_COLOR = Color.BLUE
        private const val DEFAULT_MINUTE_LINE_COLOR = Color.RED
        private const val DEFAULT_HOUR_LINE_COLOR = Color.BLACK
    }
/**
 * PAINTS
 * */
    private var out_circle_paint = Paint()
    private var inner_circle_paint = Paint()
    private var hour_markers_paint = Paint()
    private var second_arrow_paint = Paint()
    private var minute_arrow_paint = Paint()
    private var hour_arrow_paint = Paint()
    /**
     * ANIMATOR
     * */
    private var mAnimator: ValueAnimator? = null //important for starting animation
    /**
     * ARROWS
     * */
    private var second_arrow: Path? = null
    private var minute_arrow: Path? = null
    private var hour_arrow: Path? = null

    /**
     * CENTER COORDINATES
     * */
    private var centerX = 0f
    private var centerY = 0f

    /**
     * The variable is needed in order
     * to cool down the moment the animation repeats
     * */
    private var isAnimationRepeated = false


    /**
     * Colors for arrows, can be specified in xml
     */

    private var secondLineColor = DEFAULT_SECOND_LINE_COLOR
    private var minuteLineColor = DEFAULT_MINUTE_LINE_COLOR
    private var hourLineColor = DEFAULT_HOUR_LINE_COLOR

    init {
        setUpAttrs(attrs)
    }


    private fun setUpAttrs(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.CloakView,
            0, 0
        )

        secondLineColor = typedArray
            .getColor(R.styleable.CloakView_second_line_color, DEFAULT_SECOND_LINE_COLOR)
        minuteLineColor = typedArray
            .getColor(R.styleable.CloakView_minute_line_color, DEFAULT_MINUTE_LINE_COLOR)
        hourLineColor = typedArray
            .getColor(R.styleable.CloakView_hour_line_color, DEFAULT_HOUR_LINE_COLOR)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = Math.min(measuredWidth / 2, measuredHeight / 2)

        setMeasuredDimension(size, size)
        startAnim()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        this.centerX = width / 2f
        this.centerY = height / 2f


        out_circle_paint.apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
        }
        hour_markers_paint.apply {
            isAntiAlias = true
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
        }
        inner_circle_paint.apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        //a circle that we overlay on top of the lines
        // to get the hour markers on the dial
        val innerCircle = Path().apply {
            addCircle(centerX, centerY, OUT_CIRCLE_RADIUS - 100f, Path.Direction.CW)
        }
        //hour markers on the dial
        val hour_markers = Path().apply {
            //стрелка на 12
            moveTo(centerX, centerY + 600f)
            lineTo(centerX, centerY - 600f)
            close()

            //стрелка на 3 часа
            moveTo(centerX - 600f, centerY)
            lineTo(centerX + 600f, centerY)
            close()

            //стрелка на 1 час
            moveTo(centerX + 300, centerY - 520)
            lineTo(centerX - 320, centerY + 520)
            close()


            //стрелка на 2 час
            moveTo(centerX + 520, centerY - 300)
            lineTo(centerX - 520, centerY + 300)
            close()

            //стрелка на 5 час
            moveTo(centerX + 300, centerY + 520)
            lineTo(centerX - 300, centerY - 520)
            close()

            //стрелка на 4 час
            moveTo(centerX + 520, centerY + 300)
            lineTo(centerX - 520, centerY - 300)
            close()

        }

        //paint for second line
        second_arrow_paint.apply {
            isAntiAlias = true
            color = secondLineColor
            strokeWidth = 20f
            style = Paint.Style.STROKE
        }
        minute_arrow_paint.apply {
            isAntiAlias = true
            color = minuteLineColor
            strokeWidth = 25f
            style = Paint.Style.STROKE
        }
        hour_arrow_paint.apply {
            isAntiAlias = true
            color = hourLineColor
            strokeWidth = 35f
            style = Paint.Style.STROKE
        }

        canvas?.drawCircle(
            centerX,
            centerY,
            OUT_CIRCLE_RADIUS,
            out_circle_paint
        ) //основной круг циферблата

        canvas?.drawPath(hour_markers, hour_markers_paint) // линии для часовых отсечек

        canvas?.drawPath(
            innerCircle,
            inner_circle_paint
        ) //  внутренний круг, чтобы получились часовые отсечки

        canvas?.drawPath(second_arrow!!, second_arrow_paint) //секундная стрелка

        canvas?.drawPath(minute_arrow!!, minute_arrow_paint) //секундная стрелка

        canvas?.drawPath(hour_arrow!!, hour_arrow_paint) //секундная стрелка
    }

    fun startAnim() {

        //values indeed for animation of hour and minute arrows
        var min_line_value = 0f
        var hour_line_value = 0f

        mAnimator =
            ValueAnimator.ofFloat(0f, 6.28f) //the value will be changed to one decimal place
        mAnimator?.setInterpolator(LinearInterpolator())
        mAnimator?.setDuration(ANIMATION_DURATION) //in milliseconds
        mAnimator?.addUpdateListener(this)
        mAnimator?.repeatCount = Animation.INFINITE
        mAnimator?.doOnRepeat {

            /**
             * Since doOnRepeat is executed first,
             * and then the AnimationUpdate was injected isAnimationRepeated
             * In order for the minute and hour hands to be drawn at the very beginning with zero values,
             * but moved only after repeating the animation of the second hand
             */
            isAnimationRepeated = true

            if (min_line_value.toInt() >= 6.4f.toInt()) {
                if (hour_line_value.toInt() >= 6.4f.toInt()) {
                    hour_line_value = 0f
                } else {
                    hour_line_value += 0.1f
                    animateHourArrow(hour_line_value)
                    min_line_value = 0f
                    animateMinuteArrow(min_line_value)
                }

            } else {
                min_line_value += 0.1f
                animateMinuteArrow(min_line_value)
            }
        }

        mAnimator?.start()
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        var value = animation?.animatedValue as Float

        value =
            (Math.round(value * 10.0) / 10.0).toFloat() //преобразуем значение, чтобы не было плавной анимаии секундной стрелки
        //Log.e(LOG_TAG, "value = $value")

        second_arrow?.reset()

        var x_start = centerX + (OUT_CIRCLE_RADIUS - 150f) * Math.sin(value.toDouble()).toFloat()
        var y_start = centerY  - (OUT_CIRCLE_RADIUS - 150f) * Math.cos(value.toDouble()).toFloat()

        var x_end = centerX - (OUT_CIRCLE_RADIUS - 500f) *  Math.sin(value.toDouble()).toFloat()
        var y_end = centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(value.toDouble()).toFloat()

        second_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
//        if the animation doesn't repeat
//        draw the hour and minute hands at the starting position
        if (!isAnimationRepeated) {
            animateMinuteArrow(0f)
            animateHourArrow(0f)
        }


        invalidate()
    }

    private fun animateMinuteArrow(value: Float) {
        minute_arrow?.reset()

        var x_start = centerX + (OUT_CIRCLE_RADIUS - 250f) * Math.sin(value.toDouble()).toFloat()
        var y_start = centerY  - (OUT_CIRCLE_RADIUS - 250f) * Math.cos(value.toDouble()).toFloat()

//            var y_end = getEndY(x_start, y_start)
//            var x_end = getEndX(x_start)
//        Log.d("mlog", "$y_end")

        var x_end = centerX - (OUT_CIRCLE_RADIUS - 500f) *  Math.sin(value.toDouble()).toFloat()
        var y_end = centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(value.toDouble()).toFloat()

        minute_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
        invalidate()
    }

    private fun animateHourArrow(value: Float) {
        hour_arrow?.reset()

        var x_start = centerX + (OUT_CIRCLE_RADIUS - 350f) * Math.sin(value.toDouble()).toFloat()
        var y_start = centerY  - (OUT_CIRCLE_RADIUS - 350f) * Math.cos(value.toDouble()).toFloat()

        var x_end = centerX - (OUT_CIRCLE_RADIUS - 500f) *  Math.sin(value.toDouble()).toFloat()
        var y_end = centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(value.toDouble()).toFloat()


        hour_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
        invalidate()
    }

    /** value - get from ValueAnimator,
     * degreesLengthLine - the value by which you want
     *      to reduce the length of the line relative to OUT_CIRCLE_RADIUS
     *
     * return indeed X coordinate
     */
//    private fun getX_startCoordinate(value : Float, degreesLengthLine : Float) : Float{
//        return centerX + (OUT_CIRCLE_RADIUS - degreesLengthLine) * Math.sin(value.toDouble()).toFloat()
//    }
//
//    /**
//     * value - get from ValueAnimator,
//     * degreesLengthLine - the value by which you want
//     *                  to reduce the length of the line relative to OUT_CIRCLE_RADIUS
//     *
//     * return indeed Y coordinate
//     */
//    private fun getY_startCoordinate(value : Float, degreesLengthLine : Float) : Float{
//        return centerY - (OUT_CIRCLE_RADIUS - degreesLengthLine) * Math.cos(value.toDouble()).toFloat()
//    }
//
//    private fun getX_endCoordinate(value : Float, degreesLengthLine : Float) : Float{
//        return centerX + (OUT_CIRCLE_RADIUS - degreesLengthLine) * Math.sin(value.toDouble()).toFloat()
//    }
//
//    private fun getY_endCoordinate(value : Float, degreesLengthLine : Float) : Float{
//        return centerY - (OUT_CIRCLE_RADIUS - degreesLengthLine) * Math.cos(value.toDouble()).toFloat()
//    }


}