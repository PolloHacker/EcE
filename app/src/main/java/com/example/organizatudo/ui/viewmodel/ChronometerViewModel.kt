package com.example.organizatudo.ui.viewmodel


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.organizatudo.notifications.setAlarm
import com.example.organizatudo.timeManagers.Constant
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ChronometerViewModel : ViewModel() {
    private var job: Job? = null

    private val _times = MutableStateFlow(0L)
    val times = _times.asStateFlow()

    private var first : Boolean = false
    private val _mode = MutableStateFlow(true)
    val mode = _mode.asStateFlow()

    private val _cycles = MutableStateFlow(1F)
    val cycles = _cycles.asStateFlow()


    private fun changeMinutes(min: Long) {
        _times.value = min
    }

    fun startUp(withDelay: Long = 350) {
        job?.cancel()
        if(first) _times.value = 0
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(timeMillis = withDelay)
            while (isActive) {
                delay(timeMillis = Constant.MILISECOND_TICKER)
                _times.value += Constant.MILISECOND_TICKER
            }
        }
    }

    fun startDown(withDelay: Long = 350, context: Context, alarm: Int){
        if(first) {
            job?.cancel()
            first = false
        }
        if(_times.value <= 25 * 60_000) changeMinutes(5 * 60_000)
        else if(_times.value <= 50 * 60_000) changeMinutes(8 * 60_000)
        else if(_times.value <= 90 * 60_000) changeMinutes(10 * 60_000)
        else changeMinutes(15 * 60_000)
        job = viewModelScope.launch(Dispatchers.IO) {
            delay(timeMillis = withDelay)
            while (isActive) {
                if (_times.value <= 60L) {
                    _cycles.value += 1F
                    stop()
                    setAlarm(
                        context = context, delay = 0,
                        title = "Flow Time",
                        description = "O tempo acabou!",
                        alarm = alarm)
                    _times.value = 60L

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

    fun reset() {
        first = true
        job?.cancel()
        _mode.value = !_mode.value
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}