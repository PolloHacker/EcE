package com.example.organizatudo.timeManagers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.organizatudo.R
import com.example.organizatudo.ui.theme.OrganizaTudoTheme
import com.example.organizatudo.ui.theme.background
import com.example.organizatudo.ui.viewmodel.InTimeViewModel
import com.example.organizatudo.ui.viewmodel.StopWatchViewModel

@Composable
fun StopWatchApp(viewModel: StopWatchViewModel, workTime: Long, freeTime: Long, longFreeTime : Long) {
    val times by viewModel.times.collectAsState()
    val last by viewModel.last.collectAsState()
    val cycle by viewModel.cycles.collectAsState()
    val context = LocalContext.current

    OrganizaTudoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            Column {
                Box(modifier = Modifier.weight(3.0f, true)) {
                    TimerGraphic(time = times, cycle = cycle, last = last)
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ActionButtons(
                    // TODO implement ringtone selection
                    onStart = { viewModel.start(
                        work = workTime,
                        free = freeTime,
                        longFree = longFreeTime,
                        context = context,
                        alarm = R.raw.alarm14
                    ) },
                    onStop = { viewModel.stop() },
                    onReset = { viewModel.skip(work = workTime, free = freeTime, longFree = longFreeTime) },
                    labelReset = "PULAR",
                )
            }
        }
    }
}

@Composable
fun TimerApp(viewModel: InTimeViewModel, workTime: Long) {

    val times by viewModel.times.collectAsState()
    val context = LocalContext.current

    OrganizaTudoTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            Column {
                Box(modifier = Modifier.weight(3.0f, true)) {
                    InTimeGraphic(time = times)
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ActionButtons(
                    //TODO implement ringtone selection
                    onStart = { viewModel.start(
                        context = context,
                        workTime = workTime,
                        alarm = R.raw.alarm14
                    ) },
                    onStop = { viewModel.stop() },
                    onReset = { viewModel.reset(context = context, alarm = R.raw.crowd_cheering) },
                    labelReset = "TERMINAR",
                )
            }
        }
    }
}

@Composable
fun InTimeGraphic(time: Long) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Chrono(seconds = ((time / 1000) % 60).toInt().inc(), primaryChrono = true)
        Chrono(seconds = (time % 60).toInt().inc(), primaryChrono = false)
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = Formatter.getTimerFormat(time),
                style = MaterialTheme.typography.subtitle1,
                fontSize = 54.sp,
                color = Color.White
            )
            Text(
                text = (
                        "Trabalhe"
                        ),
                style = MaterialTheme.typography.subtitle2,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}