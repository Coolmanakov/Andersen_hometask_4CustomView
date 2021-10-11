package com.example.andersen_hometask_4customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import java.util.*

class ClockView @JvmOverloads constructor(context: Context, attrs: AttributeSet) :
    View(context, attrs) {

    companion object {
        private var OUT_CIRCLE_RADIUS = 600f

        private const val DEFAULT_SECOND_LINE_COLOR = Color.BLUE
        private const val DEFAULT_MINUTE_LINE_COLOR = Color.RED
        private const val DEFAULT_HOUR_LINE_COLOR = Color.BLACK
    }

    /**
     * PAINTS
     */
    private val out_circle_paint by lazy {
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
        }
    }
    private val inner_circle_paint by lazy {
        Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
    }
    private val hour_markers_paint by lazy {
        Paint().apply {
            color = Color.BLACK
            strokeWidth = 30f
            style = Paint.Style.STROKE
        }
    }
    private val second_arrow_paint by lazy {
        Paint().apply {
            color = secondLineColor
            strokeWidth = 20f
            style = Paint.Style.STROKE
        }
    }
    private val minute_arrow_paint by lazy {
        Paint().apply {
            color = minuteLineColor
            strokeWidth = 25f
            style = Paint.Style.STROKE
        }
    }
    private val hour_arrow_paint by lazy {
        Paint().apply {
            color = hourLineColor
            strokeWidth = 35f
            style = Paint.Style.STROKE
        }
    }

    /**
     * PATH
     */
    //hour markers on the dial
    private val hour_markers by lazy {
        Path().apply {

            //стрелка на 12
            moveTo(centerX, centerY + OUT_CIRCLE_RADIUS)
            lineTo(centerX, centerY - OUT_CIRCLE_RADIUS)
            close()

            //стрелка на 3 часа
            moveTo(centerX - OUT_CIRCLE_RADIUS, centerY)
            lineTo(centerX + OUT_CIRCLE_RADIUS, centerY)
            close()

            //стрелка на 1 час
            moveTo(getX_startCoordMarker(Math.PI / 3), getY_startCoordMarker(Math.PI / 3))
            lineTo(getX_endCoordMarker(Math.PI / 3), getY_endCoordMarker(Math.PI / 3))
            close()


            //стрелка на 2 час
            moveTo(getX_startCoordMarker(Math.PI / 6), getY_startCoordMarker(Math.PI / 6))
            lineTo(getX_endCoordMarker(Math.PI / 6), getY_endCoordMarker(Math.PI / 6))
            close()

            //стрелка на 5 час
            moveTo(getX_startCoordMarker(5 * Math.PI / 6), getY_startCoordMarker(5 * Math.PI / 6))
            lineTo(getX_endCoordMarker(5 * Math.PI / 6), getY_endCoordMarker(5 * Math.PI / 6))
            close()

            //стрелка на 4 час
            moveTo(getX_startCoordMarker(2 * Math.PI / 3), getY_startCoordMarker(2 * Math.PI / 3))
            lineTo(getX_endCoordMarker(2 * Math.PI / 3), getY_endCoordMarker(2 * Math.PI / 3))
            close()

        }
    }

    //a circle that we overlay on top of the lines
    // to get the hour markers on the dial
    private val innerCircle by lazy {
        Path().apply {
            addCircle(centerX, centerY, OUT_CIRCLE_RADIUS - 100f, Path.Direction.CW)

        }
    }

    /**
     * ARROWS
     * */
    private var second_arrow: Path? = null
    private var minute_arrow: Path? = null
    private var hour_arrow: Path? = null

    /**
     * CENTER COORDINATES
     */
    private var centerX = 0f
    private var centerY = 0f

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
            attrs, R.styleable.ClockView, 0, 0
        )

        secondLineColor = typedArray
            .getColor(R.styleable.ClockView_second_line_color, DEFAULT_SECOND_LINE_COLOR)
        minuteLineColor = typedArray
            .getColor(R.styleable.ClockView_minute_line_color, DEFAULT_MINUTE_LINE_COLOR)
        hourLineColor = typedArray
            .getColor(R.styleable.ClockView_hour_line_color, DEFAULT_HOUR_LINE_COLOR)
        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val size = Math.min(measuredWidth / 2, measuredHeight / 2)

        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        centerX = width / 2f
        centerY = height / 2f

        var calendar = Calendar.getInstance()
        var currentHour = calendar.get(Calendar.HOUR)
        var currentMinutes = calendar.get(Calendar.MINUTE)
        var currentSeconds = calendar.get(Calendar.SECOND)


        animateSecondArrow(currentSeconds.toFloat())
        animateMinuteArrow(currentMinutes.toFloat(), currentSeconds.toFloat())
        animateHourArrow(currentHour.toFloat(), currentMinutes.toFloat())

        canvas?.drawCircle(
            centerX,
            centerY,
            OUT_CIRCLE_RADIUS,
            out_circle_paint
        ) //main circle of cloak

        canvas?.drawPath(hour_markers, hour_markers_paint) // линии для часовых отсечек

        canvas?.drawPath(
            innerCircle,
            inner_circle_paint
        ) //  inner circle

        canvas?.drawPath(second_arrow!!, second_arrow_paint) //секундная стрелка

        canvas?.drawPath(minute_arrow!!, minute_arrow_paint) //секундная стрелка

        canvas?.drawPath(hour_arrow!!, hour_arrow_paint) //секундная стрелка
        invalidate()
    }

    private fun animateSecondArrow(value: Float) {
        var x_start =
            centerX + (OUT_CIRCLE_RADIUS - 150f) * Math.sin(Math.PI / 30 * value).toFloat()
        var y_start =
            centerY - (OUT_CIRCLE_RADIUS - 150f) * Math.cos(Math.PI / 30 * value).toFloat()

        var x_end =
            centerX - (OUT_CIRCLE_RADIUS - 500f) * Math.sin(Math.PI / 30 * value).toFloat()
        var y_end =
            centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(Math.PI / 30 * value).toFloat()

        second_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
    }

    private fun animateMinuteArrow(currentMinute: Float, currentSeconds: Float) {
        minute_arrow?.reset()

        var x_start =
            centerX + (OUT_CIRCLE_RADIUS - 250f) * Math.sin(Math.PI / 30 * (currentMinute + currentSeconds / 60))
                .toFloat()
        var y_start =
            centerY - (OUT_CIRCLE_RADIUS - 250f) * Math.cos(Math.PI / 30 * (currentMinute + currentSeconds / 60))
                .toFloat()


        var x_end =
            centerX - (OUT_CIRCLE_RADIUS - 500f) * Math.sin(Math.PI / 30 * (currentMinute + currentSeconds / 60))
                .toFloat()
        var y_end =
            centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(Math.PI / 30 * (currentMinute + currentSeconds / 60))
                .toFloat()

        minute_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
        invalidate()
    }

    private fun animateHourArrow(currentHours: Float, currentMinute: Float) {
        hour_arrow?.reset()

        var x_start =
            centerX + (OUT_CIRCLE_RADIUS - 350f) * Math.sin(Math.PI / 6 * (currentHours + currentMinute / 60))
                .toFloat()
        var y_start =
            centerY - (OUT_CIRCLE_RADIUS - 350f) * Math.cos(Math.PI / 6 * (currentHours + currentMinute / 60))
                .toFloat()

        var x_end =
            centerX - (OUT_CIRCLE_RADIUS - 500f) * Math.sin(Math.PI / 6 * (currentHours + currentMinute / 60))
                .toFloat()
        var y_end =
            centerY + (OUT_CIRCLE_RADIUS - 500f) * Math.cos(Math.PI / 6 * (currentHours + currentMinute / 60))
                .toFloat()

        hour_arrow = Path().apply {
            moveTo(x_start, y_start)
            lineTo(x_end, y_end)
            close()
        }
        invalidate()
    }

    private fun getX_startCoordMarker(value: Double): Float =
        centerX + OUT_CIRCLE_RADIUS * Math.cos(value).toFloat()

    private fun getY_startCoordMarker(value: Double): Float =
        centerY - OUT_CIRCLE_RADIUS * Math.sin(value).toFloat()

    private fun getX_endCoordMarker(value: Double): Float =
        centerX - OUT_CIRCLE_RADIUS * Math.cos(value).toFloat()

    private fun getY_endCoordMarker(value: Double): Float =
        centerY + OUT_CIRCLE_RADIUS * Math.sin(value).toFloat()

}