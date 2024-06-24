package com.busanit.subway_project.fragment

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.subway_project.R
import com.busanit.subway_project.RouteCheckActivity
import com.busanit.subway_project.adapter.StationScheduleAdapter
import com.busanit.subway_project.alarm.TimerCallback
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding
import com.busanit.subway_project.isEng
import com.busanit.subway_project.model.Line
import com.busanit.subway_project.model.StationSchedule
import com.busanit.subway_project.model.SubwayResult
import java.util.Calendar

class MinimumTransferFragment : Fragment() {

    private lateinit var binding: FragmentMinimumTransferBinding
    private lateinit var stations: MutableList<StationSchedule>
    private lateinit var stationList: MutableList<StationSchedule>
    private lateinit var intermediateStations: MutableList<StationSchedule>
    private lateinit var adapter: StationScheduleAdapter
    private var minTransferData: SubwayResult? = null   // 메인으로부터 받은 데이터 값

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        minTransferData = arguments?.getParcelable("minTransferResult")

        minTransferData?.let {

            // "00분 소요" 텍스트 뷰
            val totalTime = it.totalTime
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
                    val selectedTime = String.format("%02d : %02d", selectedHour, selectedMinute)

                    if (isEng) {
                        binding.setTime.text = "Departure Time : ${selectedTime}"
                    } else {
                        binding.setTime.text = "출발 시간 : ${selectedTime}"
                    }
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
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

//            intent.extras?.let {
//                totalSeconds = it.getInt(MainActivity.EXTRA_MINUTES, 0)
//            }

            var totalSeconds = 10   // 임의의 초

            // CountDownTimer 설정
            timer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    // 매 초마다 호출
                    val hoursRemaining = millisUntilFinished / 1000 / 3600
                    val minutesRemaining = millisUntilFinished / 1000 / 60
                    val secondsRemaining = (millisUntilFinished / 1000) % 60

                    // 버튼의 텍스트("타이머 설정")를 남은 시간으로 업데이트
                    binding.setTimer.text = String.format("%02d : %02d : %02d", hoursRemaining, minutesRemaining, secondsRemaining)
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

                val stnSchedule = StationSchedule(scode.toInt(), sname, lineCd) // StationSchedule 객체 생성

                stationList.add(stnSchedule)    // stationList에 추가
            }
        }

        // 1. 출발역 설정
        binding.startStationTextView.text = stationList.first().sname
        when (stationList.first().line.lineCd) {
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

        // "지금 가장 빠른 열차는 00:00" 시간 설정
        val startTime: String = setTime("13:50:00");
        binding.startTimeTextView.text = startTime

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
        binding.endStationText.text = stationList.last().sname
        when (stationList.last().line.lineCd) {
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

        // "도착 예정 시간은 00:00" 시간 설정
        val endTime: String = setTime("14:00:00")
        binding.endTimeTextView.text = endTime
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
}