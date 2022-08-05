package com.example.organizatudo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizatudo.notifications.setAlarm
import com.example.organizatudo.timeManagers.Constant
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InTimeViewModel : ViewModel() {
    private var job: Job? = null

    private val _times = MutableStateFlow(60L)
    val times = _times.asStateFlow()

    private var first : Boolean = false


    private fun changeMinutes(min: Long = 60L) {
        _times.value = min
    }

    fun start(withDelay: Long = 350, context: Context, workTime: Long, alarm: Int) {
        if(first) {
            job?.cancel()
            first = false
        }
        changeMinutes(workTime)
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(timeMillis = withDelay)
            while (isActive) {
                if (_times.value <= 60L) {
                    stop()
                    _times.value = 60L
                    setAlarm(
                        context = context, delay = 0,
                        title = "Just In Time",
                        description = "O tempo acabou!",
                        alarm = alarm)
                }
                delay(timeMillis = Constant.MILISECOND_TICKER)
                _times.value -= Constant.MILISECOND_TICKER
            }
        }
    }

    fun stop() {
        first = true
        job?.cancel()
    }

    fun reset(context: Context, alarm: Int) {
        first = true
        changeMinutes()
        setAlarm(context,
            0,
            "Parabéns!",
            "Você terminou antes do tempo acabar",
            alarm)
    }
}