package com.busanit.subway_project

import android.app.SearchManager
import android.content.Context
import android.graphics.Matrix
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RelativeLayout
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.github.angads25.toggle.widget.LabeledSwitch
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

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바를 액션바로 설정

        // Matrix 변화 감지 리스너 등록
        photoView.setOnMatrixChangeListener {
            updateButtonPositions()
        }

        // 각 역 버튼에 이벤트 등록
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_search, menu)

        // SearchView 설정
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        // 토글 버튼 설정
        val toggleItem = menu.findItem(R.id.action_toggle)
        val labeledSwitch = toggleItem.actionView?.findViewById<LabeledSwitch>(R.id.lan_switch)

        labeledSwitch?.setOnToggledListener { labeledSwitch, isOn ->
            // 토글 상태 변경 시 처리할 로직
            if (isOn) {
                photoView.setImageResource(R.drawable.busan_metro_eng)
            } else {
                photoView.setImageResource(R.drawable.busan_metro_kor)
            }
        }
        toggleItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_toggle -> {
                // 토글 버튼을 클릭했을 때 동작 구현
                Toast.makeText(this, "클릭!", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    // 버튼 위치 업데이트 함수
// updateButtonPositions : photoView 에서 이미지를 확대, 축소 및 이동할 때 버튼의 위치를 업데이트 하는 역할
    private fun updateButtonPositions() {   // 현재 변환 상태를 기반으로 버튼들의 위치를 업데이트
        // 1. 현재 화면의 매트릭스 가져옴 : photoView에 적용된 변환(확대, 축소, 이동) 정보를 가지고 있음
        val matrix = Matrix()
        photoView.getDisplayMatrix(matrix)
        // 2. Matrix 값을 배열에 저장
        val matrixValues = FloatArray(9)    // 3 X 3 매트릭스를 배열 형태로 나타냄
        matrix.getValues(matrixValues)

        // 3. 버튼 위치 업데이트 : 각 버튼의 원래 위치에 matrix 변환 적용
        updateButtonPosition(binding.station1, 1300f,680f, matrixValues)

        // 추가 역 버튼들에 대해서도 동일하게 처리
    }
    // updateButtonPosition : 원래 이미지 좌표를 기반으로 매트릭스 변환을 적용하여 버튼의 위치를 업데이트 하는 함수
    private fun updateButtonPosition(button: Button, origX: Float, origY: Float, matrixValues: FloatArray) {
        // 1. Matrix 변환 적용
        // scaledX, scaledY : 새로운 좌표
        // origX, origY : 원래 좌표
        // Matrix.MSCALE_X, Matrix.MSCALE_Y : 확대 / 축소 값
        // Matrix.MTRANS_X, Matrix.MTRANS_Y : 이동 값
        val scaledX = origX * matrixValues[Matrix.MSCALE_X] + matrixValues[Matrix.MTRANS_X]
        val scaledY = origY * matrixValues[Matrix.MSCALE_Y] + matrixValues[Matrix.MTRANS_Y]
        // 2. 버튼 위치 설정
        // 계산된 새로운 위치 값을 버튼의 x, y 속성에 설정 : 버튼이 이미지의 변환 상태에 맞춰 정확히 위치하도록 함
        button.x = scaledX
        button.y = scaledY
    }
}
