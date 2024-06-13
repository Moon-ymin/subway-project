package com.busanit.subway_project

import android.app.SearchManager
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
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
import com.busanit.subway_project.helper.DatabaseHelper
import com.github.angads25.toggle.widget.LabeledSwitch
import com.github.chrisbanes.photoview.PhotoView
import org.jsoup.Jsoup
import java.io.IOError
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var buttonsContainer: RelativeLayout
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoView = binding.photoView
        buttonsContainer = binding.buttonsContainer

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바를 액션바로 설정

        // HTML 파일에서 데이터 읽기
        dbHelper = DatabaseHelper(this)
        parseHtmlAndInsertData()

        // 예제: 클릭 이벤트 처리
        photoView.setOnPhotoTapListener { view, x, y ->
            // x, y는 이미지의 상대적인 좌표 (0.0 ~ 1.0)
            val drawable = photoView.drawable
            if (drawable != null ) {
                val imageMatrix = Matrix(photoView.imageMatrix)
                val imageRect = RectF(0f, 0f, drawable.intrinsicWidth.toFloat(), drawable.intrinsicHeight.toFloat())
                imageMatrix.mapRect(imageRect)

                // 변환된 좌표
                val absoluteX = (imageRect.left + x * imageRect.width()).toInt()
                val absoluteY = (imageRect.top + y * imageRect.height()).toInt()

                // 로그 출력
                Log.d("PhotoView", "Relative coordinates: ($x, $y)")
                Log.d("PhotoView", "Absolute coordinates: ($absoluteX, $absoluteY)")

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

    // HTML 파서
    private fun parseHtmlAndInsertData() {
        try {
            val inputStream = assets.open("station_points.html")
            val doc = Jsoup.parse(inputStream, "UTF-8", "")
            val areas = doc.select("area")

            val db = dbHelper.writableDatabase
            db.beginTransaction()
            try {
                for (area in areas) {
                    val title = area.attr("title")
                    val coords = area.attr("coords").split(",")
                    val x1 = coords[0].toInt()
                    val y1 = coords[1].toInt()
                    val x2 = coords[2].toInt()
                    val y2 = coords[3].toInt()

                    val values = ContentValues().apply {
                        put(DatabaseHelper.COLUMN_TITLE, title)
                        put(DatabaseHelper.COLUMN_X1, x1)
                        put(DatabaseHelper.COLUMN_Y1, y1)
                        put(DatabaseHelper.COLUMN_X2, x2)
                        put(DatabaseHelper.COLUMN_Y2, y2)
                    }
                    db.insert(DatabaseHelper.TABLE_NAME, null, values)
                }
            } finally {
                db.endTransaction()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    // 역 클릭해서 역 이름 뜨는 알림 띄워보기
    private fun handleImageClick(x: Int, y: Int) {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null)

        var foundStation = false

        if (cursor.moveToFirst()) {
            do {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE))
                val x1 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_X1))
                val y1 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Y1))
                val x2 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_X2))
                val y2 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_Y2))

                if (x in x1..x2 && y in y1..y2) {
                    Toast.makeText(this, "Station: $title", Toast.LENGTH_SHORT).show()
                    foundStation = true
                    break
                }
            } while (cursor.moveToNext())
        }

        cursor.close()

        if (!foundStation) {
            Toast.makeText(this, "No station $x, $y", Toast.LENGTH_SHORT).show()
        }
    }
}
