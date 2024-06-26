package com.busanit.subway_project_watch.presentation

import android.content.Intent
import android.os.CountDownTimer
import android.util.Log
import com.busanit.subway_project_watch.databinding.ActivityTimerBinding
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class DataLayerListenerService : WearableListenerService() {
    private var isTimerRunning = false
    private var countDownTimer: CountDownTimer? = null
    private lateinit var binding: ActivityTimerBinding


    override fun onDataChanged(dataEvents: DataEventBuffer) {
        super.onDataChanged(dataEvents)

        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val path = event.dataItem.uri.path
                if ("/timer" == path) {
                    // 데이터 아이템에서 데이터 추출
                    val dataMapItem = DataMapItem.fromDataItem(event.dataItem)
                    val timeRemaining = dataMapItem.dataMap.getLong("timeRemaining")
                    Log.d("WatchTimer", "Received timeRemaining: $timeRemaining")

                    // 데이터를 받고 UI를 업데이트하는 코드 추가
                    updateUI(timeRemaining)
                }
            }
        }
    }

    private fun updateUI(timeRemaining: Long) {
        if (timeRemaining > 0) {
            startTimer(timeRemaining)
        } else {
            // 타이머가 종료된 경우
            stopTimer()
        }
    }

    private fun startTimer(timeInMillis: Long) {
        countDownTimer?.cancel() // 기존 타이머가 있다면 취소

        countDownTimer = object : CountDownTimer(timeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val hoursRemaining = millisUntilFinished / 1000 / 3600
                val minutesRemaining = (millisUntilFinished / 1000 % 3600) / 60
                val secondsRemaining = (millisUntilFinished / 1000) % 60

                // UI 업데이트
                val timeText = String.format("%02d : %02d : %02d", hoursRemaining, minutesRemaining, secondsRemaining)
                binding.setTimer.text = timeText
            }

            override fun onFinish() {
                // 타이머 종료 시 처리
                binding.setTimer.text = "타이머 종료"
                isTimerRunning = false
            }
        }.start()

        isTimerRunning = true
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        binding.setTimer.text = "타이머 종료"
        isTimerRunning = false
    }

}

