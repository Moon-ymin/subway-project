package com.busanit.subway_project

import android.app.AlertDialog
import android.content.Context
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Button
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.github.chrisbanes.photoview.PhotoView

class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var buttonsContainer: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        photoView = findViewById(R.id.photo_view)
        buttonsContainer = findViewById(R.id.buttons_container)

        // Matrix 변화 감지 리스너 등록
        photoView.setOnMatrixChangeListener {
            updateButtonPositions()
        }

        // 각 버튼에 이벤트 등록
        val station1: Button = findViewById(R.id.station1)
        station1.setOnClickListener {
            // 역 버튼 클릭 시 이벤트 처리
            showStationOptions(this, station1)
        }

        // 추가 역 버튼들에 대해서도 동일하게 처리

    }

    // 버튼 위치 업데이트 함수
    private fun updateButtonPositions() {
        val matrix = Matrix()
        photoView.getDisplayMatrix(matrix)

        val matrixValues = FloatArray(9)
        matrix.getValues(matrixValues)

        // 각 버튼의 원래 위치에 matrix 변환 적용
        updateButtonPosition(findViewById(R.id.station1), 100f, 200f, matrixValues)

        // 추가 역 버튼들에 대해서도 동일하게 처리
    }

    private fun updateButtonPosition(button: Button, origX: Float, origY: Float, matrixValues: FloatArray) {
        val scaledX = origX * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X]
        val scaledY = origY * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y]
        button.x = scaledX
        button.y = scaledY
    }

    private fun showStationOptions(context: Context, stationButton: Button) {
        val options = arrayOf("출발", "경유", "도착")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("선택")
            .setItems(options) { dialog, which ->
                // 선택된 항목 처리
                when (which) {
                    0 -> {
                        // 출발
                    }
                    1 -> {
                        // 경유
                    }
                    2 -> {
                        // 도착
                    }
                }
            }
        builder.create().show()
    }
}
