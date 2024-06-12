package com.busanit.subway_project

import android.app.AlertDialog
import android.content.Context
import android.graphics.Matrix
import android.os.Bundle
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.github.chrisbanes.photoview.PhotoView

class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var buttonsContainer: RelativeLayout
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoView = binding.photoView
        buttonsContainer = binding.buttonsContainer

        // Matrix 변화 감지 리스너 등록
        photoView.setOnMatrixChangeListener {
            updateButtonPositions()
        }

        // 각 버튼에 이벤트 등록
        val station1: Button = binding.station1
        station1.setOnClickListener {view ->
            // 역 버튼 클릭 시 이벤트 처리
            val popupMenu = PopupMenu(applicationContext, view)
            popupMenu.inflate(R.menu.main_popup)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu1 -> Toast.makeText(this@MainActivity, "출발!", Toast.LENGTH_SHORT).show()
                    R.id.menu2 -> Toast.makeText(this@MainActivity, "경유!", Toast.LENGTH_SHORT).show()
                    else -> Toast.makeText(this@MainActivity, "도착!", Toast.LENGTH_SHORT).show()
                }
                true
            }
            popupMenu.show()
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
        updateButtonPosition(binding.station1, 700f, 500f, matrixValues)

        // 추가 역 버튼들에 대해서도 동일하게 처리
    }

    private fun updateButtonPosition(button: Button, origX: Float, origY: Float, matrixValues: FloatArray) {
        val scaledX = origX * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X]
        val scaledY = origY * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y]
        button.x = scaledX
        button.y = scaledY
    }


}
