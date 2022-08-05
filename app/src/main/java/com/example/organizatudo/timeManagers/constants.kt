package com.example.organizatudo.timeManagers

object Constant {
    const val MILISECOND_TICKER = 64L
    const val TIME_IN_SECOND = 60
}

object Formatter {

    fun getTimerFormat(timeInMilies: Long): String {
        val timeInSec = timeInMilies.div(1000)
        val minutes = (timeInSec % 3600) / 60
        val second = timeInSec % 60
        val milSec = timeInMilies % 60
        return String.format("%02d:%02d:%02d", minutes, second, milSec)
    }
}

data class Lap(
    val time: Long,
    val diff: Long,
    val chart: Int

) {
    companion object {
        const val STATE = 0
        const val UP = 1
        const val DOWN = 2
    }
}