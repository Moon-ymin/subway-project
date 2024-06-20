package com.busanit.subway_project.fragment

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.busanit.subway_project.R
import com.busanit.subway_project.adapter.StationAdapter
import com.busanit.subway_project.alarm.AlarmReceiver
import com.busanit.subway_project.databinding.FragmentMinimumTransferBinding
import com.busanit.subway_project.model.Line
import com.busanit.subway_project.model.Station
import java.util.Calendar

class MinimumTransferFragment : Fragment() {

    private lateinit var binding: FragmentMinimumTransferBinding
    private lateinit var stations: List<Station>
    private lateinit var allStations: List<Station>
    private lateinit var intermediateStations: List<Station>
    private lateinit var adapter: StationAdapter

    // 타이머 관련
    private var timer: CountDownTimer? = null

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

        // "00분 소요" 텍스트 뷰
        val time = calculateTime()
        binding.timeInfoTextView1.text = "${time}분"

        // "00개 역 이동" 텍스트 뷰
        val stations = calculateTotalStations()
        binding.totalStationTextView.text = "${stations}개 역 이동"

        // 시간 설정 버튼 → 사용자가 직접 시간 설정
        binding.setTime.setOnClickListener {

            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d : %02d", selectedHour, selectedMinute)
                    binding.setTime.text = "출발 $selectedTime"
                },
                hour,
                minute,
                true
            )
            timePickerDialog.show()
        }

        // 타이머 설정 버튼
        binding.setTimer.setOnClickListener {

            timer?.cancel() // 기존 타이머가 있다면 취소

//            intent.extras?.let {
//                totalSeconds = it.getInt(MainActivity.EXTRA_MINUTES, 0)
//            }

            var totalSeconds = 30

            // CountDownTimer 설정
            timer = object : CountDownTimer((totalSeconds * 1000).toLong(), 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    // 매 초마다 호출
                    val hoursRemaining = millisUntilFinished / 1000 / 3600
                    val minutesRemaining = millisUntilFinished / 1000 / 60
                    val secondsRemaining = (millisUntilFinished / 1000) % 60

                    // 버튼의 텍스트를 남은 시간으로 업데이트
                    binding.setTimer.text = String.format("%02d : %02d : %02d", hoursRemaining, minutesRemaining, secondsRemaining)
                }

                override fun onFinish() {
                    setAlarm()

                    // 타이머 종료 시 호출
                    binding.setTimer.text = "타이머 종료"

                    // 여기에 타이머가 종료된 후
                    timer?.cancel()
                }
            }

            // 타이머 시작
            (timer as CountDownTimer).start()
            }

        setUpRecyclerView()
    }

    // 총 소요 시간 계산 메서드
    private fun calculateTime(): Int {
        return 6;
    }

    // 총 경유 역 개수 계산 메서드
    private fun calculateTotalStations(): Int {
        return 4;
    }

    // 출발역 | 중간역 | 도착역 리사이클러 뷰 세팅
    private fun setUpRecyclerView() {

        allStations = listOf(
            Station(100, "금련산역", Line(1, "1호선"), 0),
            Station(101, "남천역", Line(2, "1호선"), 0),
            Station(102, "경성대부경대역", Line(3, "1호선"), 0),
            Station(103, "대연역", Line(4, "1호선"), 0),
            Station(104, "못골역", Line(8, "2호선"), 0),
            Station(105, "지게골역", Line(9, "2호선"), 0)
        )

        // 출발역 설정
        binding.startStationTextView.text = allStations.first().sname
        when (allStations.first().line.lineCd) {
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

        intermediateStations = allStations.subList(1, allStations.size - 1)

        stations = listOf()

        adapter = StationAdapter(stations)

        binding.recyclerViewStations.layoutManager = LinearLayoutManager(context)
        binding.recyclerViewStations.adapter = adapter

        // 토글 버튼 클릭 시
        binding.toggleButton.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                // 중간 역 보이기
                stations = intermediateStations
                binding.recyclerViewStations.visibility = View.VISIBLE
            } else {
                // 중간 역 숨기기
                stations = listOf()
                binding.recyclerViewStations.visibility = View.GONE
            }
            adapter.updateStations(stations)
        }

        // 도착역 설정
        binding.endStationText.text = allStations.last().sname
        when (allStations.last().line.lineCd) {
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

    // 타이머 종료 시 알람 울리게 하는 메서드
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val name = "타이머 채널"
            val descriptionText = "타이머 채널입니다."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(AlarmReceiver.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setAlarm() {

        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val timeInMillis = Calendar.getInstance().timeInMillis + 500 // 1초 후 알람

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    // 중간 역 길이에 따라 왼쪽 바 길이 조정
//    private fun adjustLineViewHeight(itemCount: Int) {
//
//        val params = binding.lineView.layoutParams
//        params.height = itemCount * 200
//
//        binding.lineView.layoutParams = params
//    }
}