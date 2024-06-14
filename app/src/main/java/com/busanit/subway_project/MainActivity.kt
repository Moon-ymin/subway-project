package com.busanit.subway_project

import DBHelper
import android.app.SearchManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.github.angads25.toggle.widget.LabeledSwitch
import com.github.chrisbanes.photoview.PhotoView

class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoView = binding.photoView

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바를 액션바로 설정

        // HTML 파일에서 데이터 읽기
        dbHelper = DBHelper(this)

        // 클릭 이벤트 처리
        photoView.setOnPhotoTapListener { view, x, y ->
            val drawable = photoView.drawable
            if (drawable != null) {
                val imageWidth = drawable.intrinsicWidth
                val imageHeight = drawable.intrinsicHeight

                // 상대 좌표를 절대 좌표로 변환
                val absoluteX = (x * imageWidth).toInt()
                val absoluteY = (y * imageHeight).toInt()

                // 변환된 절대 좌표 출력
                Log.d("MainActivity", "Relative coordinates: ($x, $y)")
                Log.d("MainActivity", "Absolute coordinates: ($absoluteX, $absoluteY)")

                // 이후 처리 로직 구현
                handleImageClick(absoluteX, absoluteY)
            }
        }

    }

    // 상단바 설정
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_search, menu)

        // SearchView 설정
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView
        val searchManager = getSystemService(SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        // SearchView 검색 기능 구현
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 입력된 검색어 토스트 메시지로 띄우기
                Toast.makeText(this@MainActivity, "검색어 : $query", Toast.LENGTH_SHORT).show()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        // 토글 버튼 설정
        val toggleItem = menu.findItem(R.id.action_toggle)
        val labeledSwitch = toggleItem.actionView?.findViewById<LabeledSwitch>(R.id.lan_switch)

        labeledSwitch?.setOnToggledListener { _, isOn ->
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

    // 역 클릭해서 역 이름 뜨는 알림 띄워보기
    private fun handleImageClick(x: Int, y: Int) {  // 클릭 이벤트로 가져온 절대좌표
        val drawable = photoView.drawable
        if (drawable != null) {
            Log.d("MainActivity", "absoluteclick:($x, $y) ")
            // 데이터베이스에서 절대 좌표 가져오기
            val db = dbHelper.readableDatabase
            val cursor = db.query(DBHelper.TABLE_NAME, null, null, null, null, null, null)

            var foundStation = false

            if (cursor.moveToFirst()) {
                do {
                    val title = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_TITLE))
                    val x1 = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_X1)).toInt()
                    val y1 = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_Y1)).toInt()
                    val x2 = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_X2)).toInt()
                    val y2 = cursor.getFloat(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_Y2)).toInt()
                    //Log.d("MainActivity", "db Click: ($title : $x1, $y1, $x2, $y2)")

                    // 클릭한 좌표가 DB에 저장된 좌표 범위 안에 있는지 확인
                    if (x in x1..x2 && y in y1..y2) {
                        Toast.makeText(this, "Station: $title", Toast.LENGTH_SHORT).show()
                        foundStation = true
                        break
                    }
                } while (cursor.moveToNext())
            }

            cursor.close()

            if (!foundStation) {
                Toast.makeText(this, "No station found at ($x, $y)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}