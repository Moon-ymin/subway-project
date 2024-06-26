package com.busanit.subway_project.fragment

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.subway_project.R
import com.busanit.subway_project.RouteCheckActivity
import com.busanit.subway_project.adapter.StationScheduleAdapter
import com.busanit.subway_project.alarm.TimerCallback
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding
import com.busanit.subway_project.isEng
import com.busanit.subway_project.model.Line
import com.busanit.subway_project.model.LocationData
import com.busanit.subway_project.model.ResultWrapper
import com.busanit.subway_project.model.StationSchedule
import com.busanit.subway_project.model.SubwayResult
import com.busanit.subway_project.retrofit.ApiService
import com.busanit.subway_project.retrofit.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.time.Duration

class MinimumTransferFragment : Fragment() {

    private lateinit var binding: FragmentMinimumTransferBinding
    private lateinit var stations: MutableList<StationSchedule>
    private lateinit var stationList: MutableList<StationSchedule>
    private lateinit var intermediateStations: MutableList<StationSchedule>
    private lateinit var adapter: StationScheduleAdapter
    private var minTransferData: SubwayResult? = null   // 메인 액티비티로부터 받은 데이터 값
    private var arrive_time: String? = null     // 도착 시간 "HH:MM:SS"

    // 타이머 관련
    private var timer: CountDownTimer? = null

    // 알림 및 알람 관련
    private var callback: TimerCallback? = null

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = FragmentMinimumTransferBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        minTransferData = arguments?.getParcelable("minTransferResult")
        val from: Int? = arguments?.getInt("from")
        val via: Int? = arguments?.getInt("via")
        val to: Int? = arguments?.getInt("to")

        var totalTime = 0   // "00분 소요" 텍스트 뷰 및 타이머 설정을 위한 값
        minTransferData?.let {

            // "00분 소요" 텍스트 뷰
            totalTime = it.totalTime
            if (isEng) {
                binding.timeInfoTextView1.text = "Around ${totalTime / 60}min"
            } else {
                binding.timeInfoTextView1.text = "약 ${totalTime / 60}분"
            }

            // "00개 역 이동" 텍스트 뷰
            val size = it.path.size
            if (isEng) {
                binding.totalStationTextView.text = "Travel ${size} stations"
            } else {
                binding.totalStationTextView.text = "${size}개 역 이동"
            }
        }

        // "출발 시간 설정" : 사용자가 직접 시간 설정
        if (isEng) {
            binding.setTime.text = "Set Departure Time"
        }

        binding.setTime.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)

                    if (isEng) {
                        binding.setTime.text = "Departure Time : ${selectedTime}"
                    } else {
                        binding.setTime.text = "출발 시간 : ${selectedTime}"
                    }

                    val selectedLocalTime = LocalTime.of(selectedHour, selectedMinute)
                    Log.d("TimeCheck", "Selected Time: $selectedLocalTime")

                    if (from != null && via != null && to != null) {
                        sendLocationDataToServer(from, via, to, selectedTime)
                    }
                },
                hour,
                minute,
                true
            )
            timePickerDialog?.show()
        }

        // "타이머 설정" 버튼
        if (isEng) {
            binding.setTimer.text = "Set Timer"
        }

        binding.setTimer.setOnClickListener {

            val activity = requireActivity() as RouteCheckActivity

            if (activity.isTimerRunning()) {
                // 이미 타이머가 실행 중인 경우
                if (isEng) {
                    binding.setTimer.text = "Timer is already running."
                } else {
                    binding.setTimer.text = "타이머가 이미 실행 중입니다."
                }
                return@setOnClickListener
            }

            timer?.cancel() // 기존 타이머가 있다면 취소

            // CountDownTimer
            // 1. "HH:MM:SS" 형식의 문자열을 LocalTime 객체로 변환
            arrive_time = minTransferData!!.path.get(minTransferData!!.path.size -1).split("|")[3]      // 도착 시간 "HH:MM:SS"

            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
            val inputTime = LocalTime.parse(arrive_time, formatter)

            // 2. 현재 시간을 가져오기
            val now = LocalTime.now()

            // 3. 두 LocalTime 객체 간의 차이 계산(초 단위)
            val duration = Duration.between(inputTime, now).abs().seconds

            timer = object : CountDownTimer(duration * 1000, 1000) {  // duration을 밀리초 단위로 변환

                override fun onTick(millisUntilFinished: Long) {
                    // 매 초마다 호출
                    val hoursRemaining = millisUntilFinished / 1000 / 3600
                    val minutesRemaining = (millisUntilFinished / 1000 % 3600) / 60
                    val secondsRemaining = (millisUntilFinished / 1000) % 60

                    // 버튼의 텍스트("타이머 설정")를 남은 시간으로 업데이트
                    binding.setTimer.text = String.format("%02d : %02d : %02d", hoursRemaining, minutesRemaining, secondsRemaining)

                    sendTimerUpdateToWearOS(millisUntilFinished)
                }

                // 타이머 종료 후
                override fun onFinish() {
                    timer?.cancel()
                    callback?.onTimerFinished()
                    activity.setTimerRunning(false)
                }
            }

            // 타이머 시작
            (timer as CountDownTimer).start()
            activity.setTimerRunning(true)
        }

        // 리사이클러 뷰 동작
        setUpRecyclerView()
    }

    // 출발역 | 경유역 | 도착역 리사이클러 뷰 세팅
    private fun setUpRecyclerView() {

        // 영어 설정
        if (isEng) {
            binding.fastestTrainIs.text = "The fastest subway time is   "
            binding.transitStnToggleBnt.textOff = "▶ SHOW TRANSIT STATIONS"
            binding.transitStnToggleBnt.textOn = "■ HIDE TRANSIT STATIONS"
            binding.arrivedTimeIs.text = "The estimated arrival time is   "
        }

        stationList = mutableListOf<StationSchedule>()

        minTransferData?.let {
            for (path in it.path) {

                val pathSplit = path.split("|")

                val scode = pathSplit[0]    // 역 코드
                val sname = pathSplit[1]    // 역 이름
                val line = pathSplit[2]     // 호선
                val time = pathSplit[3]

                var lineName = ""
                if (line.toInt() == 1) {
                    lineName = "1호선"
                } else if (line.toInt() == 2) {
                    lineName = "2호선"
                } else if (line.toInt() == 3) {
                    lineName = "3호선"
                } else if (line.toInt() == 4) {
                    lineName = "4호선"
                } else if (line.toInt() == 8) {
                    lineName = "동해선"
                } else {
                    lineName = "부산김해경전철"
                }

                val lineCd = Line(line.toInt(), lineName)   // Line 객체 생성

                val stnSchedule = StationSchedule.Schedule(scode.toInt(), sname, lineCd, time) // StationSchedule 객체 생성

                if (stationList.size > 0) {

                    val previous = stationList.last() // 마지막 요소를 가져옴

                    if (previous is StationSchedule.Schedule && previous.sname == sname) {

                        // 도보 시간 맵
                        val walkingTime = mapOf(
                            119 to "2",
                            219 to "2",
                            123 to "2",
                            305 to "2",
                            124 to "7",
                            804 to "7",
                            125 to "6",
                            402 to "6",
                            205 to "11",
                            810 to "11",
                            208 to "1",
                            301 to "1",
                            227 to "7",
                            901 to "7",
                            233 to "2",
                            313 to "2",
                            306 to "8",
                            803 to "8",
                            309 to "1",
                            401 to "1",
                            317 to "3",
                            907 to "3"
                        )

                        // 이전 역의 scode와 Key 값이 같을 경우 "도보" 및 도보 시간 추가
                        for (wt in walkingTime) {
                            if (previous.scode == wt.key) {

                                val totalWalkingTime = wt.value

                                stationList.add(StationSchedule.Walking("도보(약 ${totalWalkingTime}분)"))
                            }
                        }
                    }
                }

                stationList.add(stnSchedule)    // stationList에 추가
            }
        }

        // 1. 출발역 설정
        if (stationList.first() is StationSchedule.Schedule) {

            val firstStation = stationList.first() as StationSchedule.Schedule
            binding.startStationTextView.text = firstStation.sname

            when (firstStation.line.lineCd) {
                1 -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line1_orange)
                    this.setText("1")
                }

                2 -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line2_green)
                    this.setText("2")
                }

                3 -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line3_brown)
                    this.setText("3")
                }

                4 -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line4_blue)
                    this.setText("4")
                }

                8 -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line8_sky)
                    this.setText("동")
                }

                else -> binding.startStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line9_purple)
                    this.setText("김")
                }
            }
        }

        // 📌 "지금 가장 빠른 열차는 00:00" 시간 설정
        minTransferData?.let {

            val pathSplit = it.path[0].split("|")

            val schedule = pathSplit[3]

            val startTime: String = setTime(schedule);
            binding.startTimeTextView.text = startTime
        }

//      //////////////////////////////////////////////////////////////////////

        // 2. 경유역 설정
        intermediateStations = stationList.subList(1, stationList.size - 1)

        stations = mutableListOf()

        adapter = StationScheduleAdapter(stations)

        binding.recyclerViewStations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewStations.adapter = adapter

        // 토글 버튼 클릭 시
        binding.transitStnToggleBnt.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                // 중간 역 보이기
                stations = intermediateStations
                binding.recyclerViewStations.visibility = View.VISIBLE
            } else {
                // 중간 역 숨기기
                stations = mutableListOf()
                binding.recyclerViewStations.visibility = View.GONE
            }
            adapter.updateStations(stations)
        }

//      //////////////////////////////////////////////////////////////////////

        // 3. 도착역 설정
        if (stationList.last() is StationSchedule.Schedule) {

            val lastStation = stationList.last() as StationSchedule.Schedule
            binding.endStationText.text = lastStation.sname

            when (lastStation.line.lineCd) {
                1 -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line1_orange)
                    this.setText("1")
                }

                2 -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line2_green)
                    this.setText("2")
                }

                3 -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line3_brown)
                    this.setText("3")
                }

                4 -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line4_blue)
                    this.setText("4")
                }

                8 -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line8_sky)
                    this.setText("동")
                }

                else -> binding.endStationLineTextView.apply {
                    this.setBackgroundResource(R.drawable.image_line9_purple)
                    this.setText("김")
                }
            }
        }

        // 📌 "도착 예정 시간은 00:00" 시간 설정
        minTransferData?.let {

            val pathSplit = it.path[it.path.size - 1].split("|")

            val schedule = pathSplit[3]

            val endTime: String = setTime(schedule);
            binding.endTimeTextView.text = endTime
        }
    }

    // "지금 가장 빠른 열차는 00:00" & "도착 예정 시간은 00:00"에서 시간 구현하는 메서드
    private fun setTime(time: String): String {

        val parts = time.split(":")

        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()
        val seconds = parts[2].toInt()

        val timeText = String.format("%02d : %02d", hours, minutes)

        return timeText
    }

    // 워치로 타이머 데이터 전달하는 메서드
    private fun sendTimerUpdateToWearOS(timeRemaining: Long) {
        val watchURL = "http://10.0.2.2:8080/"
        val retrofit = Retrofit.Builder()
            .baseUrl(watchURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)

        // 서버로 데이터 전송
        val call = service.sendTimerUpdate(timeRemaining)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // 요청 성공 처리
                    Log.d("sendTimerUpdateToWearOS", "Success: Data sent to server")
                } else {
                    // 요청 실패 처리
                    Log.e("sendTimerUpdateToWearOS", "Failed: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // 네트워크 오류 처리
                Log.e("sendTimerUpdateToWearOS", "Error: ${t.message}")
            }
        })
    }



    // 알림 및 알람 관련 메서드
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TimerCallback) {
            callback = context
        } else {
            throw RuntimeException("$context must implement TimerListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    private fun sendLocationDataToServer(from: Int, via: Int, to: Int, settingTime: String) {
        // 서버에 전송할 데이터 객체 생성
        val locationData = LocationData(from, via, to, settingTime)

        // Fragment 컨텍스트 생성
        val context = context
        // Retrofit을 통해 서버로 데이터 전송
        RetrofitClient.apiService.sendLocationData(locationData).enqueue(object :
            Callback<ResultWrapper> {
            override fun onResponse(call: Call<ResultWrapper>, response: Response<ResultWrapper>) {
                if (response.isSuccessful) {
                    // 서버로 데이터 전송 후 연산 결과 가져오기 ResultWrapper
                    Log.e("MainActivity", "get ResultWrapper From Server!! : ${response.body()}")
                    val resultWrapper = response.body()
                    resultWrapper?.let {

                        val intent = Intent(context, RouteCheckActivity::class.java).apply {
                            putExtra("minTransferResult", it.minTransferResult)
                            putExtra("minTimeResult", it.minTimeResult)
                            putExtra("from", from)
                            putExtra("via", via)
                            putExtra("to", to)
                        }
                        startActivity(intent)
                        Log.e("MainActivity", "start RouteCheckActivity!!")
                    }
                } else {
                    Toast.makeText(context, "서버로 경로 데이터 전송 실패", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResultWrapper>, t: Throwable) {
                Toast.makeText(context, "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Request failed: ${t.message}")
            }
        })
    }
}