package com.example.organizatudo.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizatudo.notifications.setAlarm
import com.example.organizatudo.timeManagers.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StopWatchViewModel : ViewModel() {
    private var job: Job? = null

    private val _times = MutableStateFlow(60L)
    val times = _times.asStateFlow()

    private var first : Boolean = false
    private var _last = MutableStateFlow(3)
    val last = _last.asStateFlow()


    private val _cycles = MutableStateFlow(1F)
    val cycles = _cycles.asStateFlow()

    private fun changeMinutes(min: Long) {
        _times.value = min
    }

    fun start(withDelay: Long = 350, work : Long, free: Long, longFree: Long, context: Context, alarm: Int) {
        if(first) {
            job?.cancel()
            first = false
        }
        if(_times.value <= 60L) {
            if(_cycles.value == 5F) {
                changeMinutes(longFree)
            } else {
                if(_last.value != 1) {
                    changeMinutes(work)
                    _cycles.value += 0.5F
                    _last.value = 1
                } else {
                    changeMinutes(free)
                    _last.value = 2
                    _cycles.value += 0.5F
                }
            }
        }
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(timeMillis = withDelay)
            while (isActive) {
                if (_times.value <= 60L) {
                    stop()
                    _times.value = 60L
                    setAlarm(
                        context = context, delay = 0,
                        title = "Pomodoro",
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

    fun skip(work : Long, free: Long, longFree: Long) {
        job?.cancel()
        if(_cycles.value.rem(5F) == 0F) {
            changeMinutes(longFree)
            _cycles.value += 0.5F
            _last.value = 0
        } else {
            if(_last.value != 1) {
                changeMinutes(work)
                _cycles.value += 0.5F
                _last.value = 1
            } else {
                changeMinutes(free)
                _last.value = 2
                _cycles.value += 0.5F
            }
        }

    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}