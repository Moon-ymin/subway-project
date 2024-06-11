package com.busanit.subway_project

// SubwayMapView.kt
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.github.chrisbanes.photoview.PhotoView

class SubwayMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    private val paint = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.holo_blue_light)
        style = Paint.Style.FILL
    }

    private val stations = listOf(
        Station("Station1", 100f, 200f),
        Station("Station2", 300f, 400f),
        Station("Station3", 500f, 600f)
    )

    init {
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (station in stations) {
            canvas.drawCircle(station.x, station.y, 20f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 터치 이벤트 처리
        return super.onTouchEvent(event)
    }

    data class Station(val name: String, val x: Float, val y: Float)
}
