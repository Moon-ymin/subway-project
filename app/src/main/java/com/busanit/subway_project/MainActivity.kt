package com.busanit.subway_project

import DBHelper
import android.app.SearchManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.busanit.subway_project.databinding.ActivityMainBinding
import com.busanit.subway_project.model.Station
import com.busanit.subway_project.retrofit.RetrofitClient
import com.github.angads25.toggle.widget.LabeledSwitch
import com.github.chrisbanes.photoview.PhotoView
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var photoView: PhotoView
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: DBHelper
    private var isEng = false   // 한 영 버전 여부 플래그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        photoView = binding.photoView

        // 상단바 구현
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // 툴바를 액션바로 설정

        // HTML 파일에서 데이터 읽기
        dbHelper = DBHelper(this)

        // 클릭 이벤트 처리 : 메인 화면 속 역 클릭하면 -> 팝업 메뉴 뜨게
        photoView.setOnPhotoTapListener { view, x, y ->
            val drawable = photoView.drawable
            if (drawable != null) {
                val imageWidth = drawable.intrinsicWidth
                val imageHeight = drawable.intrinsicHeight

                // 상대 좌표를 절대 좌표로 변환
                val absoluteX = (x * imageWidth).toInt()
                val absoluteY = (y * imageHeight).toInt()

                /*// 변환된 절대 좌표 출력
                Log.d("MainActivity", "Relative coordinates: ($x, $y)")
                Log.d("MainActivity", "Absolute coordinates: ($absoluteX, $absoluteY)")
*/
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
                // 입력된 검색어 query 은 sname -> station 테이블에서 scode 가져와서
                // scode로 -> showPopup 메서드로 띄우기
                var where: String = query.toString()
                if (!query.toString().endsWith("역")){
                    where = query.toString() + "역"
                }
                RetrofitClient.stationService.getStationBySname(where).enqueue(object : Callback<Station> {
                    override fun onResponse(call: Call<Station>, response: Response<Station>) {
                        if (response.isSuccessful) {
                            val station = response.body()
                            station?.let {
                                showPopup(binding.photoView, it.scode.toString())
                            }
                        } else {
                            Log.d("MainActivity", "Request failed: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Station>, t: Throwable) {
                        Log.d("MainActivity", "Request failed: ${t.message}")
                    }
                })
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
                isEng = true
                photoView.setImageResource(R.drawable.busan_metro_eng)
            } else {
                isEng = false
                photoView.setImageResource(R.drawable.busan_metro_kor)
            }
        }
        toggleItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return true
    }
    // 역 이름 클릭 (출발, 경유, 도착) 선택 팝업 윈도우 띄우기
    private fun showPopup(v: View, title: String) {
        // LayoutInflater를 사용하여 팝업 레이아웃 인플레이트
        val inflater: LayoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // 한 영 버전 변환
        var popupView = inflater.inflate(R.layout.main_popup_kr, null)
        if (isEng) {
            popupView = inflater.inflate(R.layout.main_popup_en, null)
        }

        // PopupWindow 생성
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // Focusable
        )

        // 역 이름 설정 : title 은 scode -> station 테이블에서 sname 가져오기
        val stationTextView = popupView.findViewById<TextView>(R.id.station)
        // Retrofit을 통해 서버에서 데이터 가져오기
        RetrofitClient.stationService.getStationByScode(title.toInt()).enqueue(object : Callback<Station> {

            override fun onResponse(call: Call<Station>, response: Response<Station>) {
                if (response.isSuccessful) {
                    val station = response.body()
                    station?.let {
                        val name = it.sname
                        stationTextView.text = name  // TextView에 sname 설정
                        Log.d("MainActivity", "Station name: $name")
                    }
                } else {
                    Log.d("MainActivity", "Request failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Station>, t: Throwable) {
                Log.e("MainActivity", "Request failed: ${t.message}")
            }
        })


        // 메뉴 아이템 클릭 이벤트 설정
        popupView.findViewById<View>(R.id.menu1).setOnClickListener {
            Toast.makeText(this, "출발!", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        popupView.findViewById<View>(R.id.menu2).setOnClickListener {
            Toast.makeText(this, "경유!", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
        popupView.findViewById<View>(R.id.menu3).setOnClickListener {
            Toast.makeText(this, "도착!", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }

        // PopupWindow의 배경을 투명하게 설정
        popupWindow.setBackgroundDrawable(ColorDrawable())
        // 배경을 어둡게 설정
        popupWindow.setOnDismissListener {
            setWindowBackgroundDim(false)
        }

        // PopupWindow의 배경을 설정하여 외부 클릭시 닫히도록 설정
        popupWindow.setBackgroundDrawable(null)

        // 팝업 창의 위치 설정
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0)
        setWindowBackgroundDim(true) // 팝업을 표시할 때 배경을 어둡게 설정
    }

    // 배경 어둡게 설정하는 메서드
    private fun setWindowBackgroundDim(dim: Boolean) {
        val window = window
        val layoutParams = window.attributes
        layoutParams.alpha = if (dim) 0.5f else 1.0f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.attributes = layoutParams
    }

    // 역 클릭해서 역 이름 뜨는 알림 띄워보기
    private fun handleImageClick(abx: Int, aby: Int) {  // 클릭 이벤트로 가져온 절대좌표
        val drawable = photoView.drawable
        if (drawable != null) {
            Log.d("MainActivity", "absoluteclick:($abx, $aby) ")
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
                    if (abx in x1..x2 && aby in y1..y2) {
                        // 역 이름 뜨는지 확인
                        //Toast.makeText(this, "Station: $title", Toast.LENGTH_SHORT).show()
                        foundStation = true
                        showPopup(binding.photoView, title)
                        break
                    }
                } while (cursor.moveToNext())
            }

            cursor.close()

            if (!foundStation) {
                Toast.makeText(this, "No station found at ($abx, $aby)", Toast.LENGTH_SHORT).show()
            }
        }
    }
}