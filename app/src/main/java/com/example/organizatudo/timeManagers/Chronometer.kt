package com.example.organizatudo.timeManagers

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.organizatudo.R
import com.example.organizatudo.ui.theme.*
import com.example.organizatudo.ui.viewmodel.ChronometerViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.nextDown
import kotlin.math.sin

@Composable
fun ChronometerApp(viewModel: ChronometerViewModel) {

    val times by viewModel.times.collectAsState()
    val mode by viewModel.mode.collectAsState()
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
                    ChronometerGraphic(times, mode, cycle)
                }
            }
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                ActionButtons(
                    // TODO implement ringtone selection
                    onStart = {
                        if(mode) viewModel.startUp()
                        else viewModel.startDown(context = context, alarm = R.raw.alarm14)
                              },
                    onStop = {
                        viewModel.stop()
                             },
                    onReset = {
                        viewModel.reset()
                              },
                    labelReset = "PULAR",
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ActionButtons(
    onStart: () -> Unit = {},
    onStop: () -> Unit = {},
    onReset: () -> Unit = {},
    labelReset: String = "RESET"
) {
    var startVisibility by remember { mutableStateOf(true) }
    var skipVisibility by remember { mutableStateOf(false) }
    var stopVisibility by remember { mutableStateOf(false) }
    val gradient = Brush.verticalGradient(listOf(background50, background, background))
    Box(
        modifier = Modifier
            .padding(bottom = 20.dp, top = 20.dp)
            .background(gradient)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = stopVisibility,
                enter = getEnterStopAnimation(),
                exit = getExitStopAnimation()
            ) {
                TimerButton(label = "PARAR", color = accent) {
                    stopVisibility = false
                    skipVisibility = false
                    startVisibility = true
                    onStop.invoke()
                }
            }
            AnimatedVisibility(
                visible = skipVisibility,
                enter = getEnterResetAnimation(),
                exit = getExitResetAnimation()
            ) {
                TimerButton(label = labelReset, color = secondary) {
                    stopVisibility = false
                    skipVisibility = false
                    startVisibility = true
                    onReset.invoke()
                }
            }
        }
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
            AnimatedVisibility(
                visible = startVisibility,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TimerButton(label = "COMEÇAR", color = primary) {
                    stopVisibility = true
                    skipVisibility = true
                    startVisibility = false
                    onStart.invoke()
                }
            }
        }
    }
}

@ExperimentalAnimationApi
internal fun getEnterStopAnimation(): EnterTransition =
    slideInHorizontally(initialOffsetX = { 120 })

@ExperimentalAnimationApi
internal fun getExitStopAnimation(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { 120 }) + fadeOut()

@ExperimentalAnimationApi
internal fun getEnterResetAnimation(): EnterTransition =
    slideInHorizontally(initialOffsetX = { -120 })

@ExperimentalAnimationApi
internal fun getExitResetAnimation(): ExitTransition =
    slideOutHorizontally(targetOffsetX = { -120 }) + fadeOut()

@Composable
internal fun TimerButton(
    label: String,
    color: Color,
    action: () -> Unit = {}
) {
    Box(modifier = Modifier.padding(start = 32.dp, end = 32.dp)) {
        Box(
            Modifier
                .background(color = color, shape = CircleShape)
                .clip(CircleShape)
                .width(80.dp)
                .aspectRatio(1f)
                .clickable(
                    onClick = action
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, style = typography.button, color = Color.White, fontSize = 14.sp)
        }
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
fun ActionButtonsPreview() {
    ActionButtons()
}

@Composable
fun ChronometerGraphic(time: Long, mode: Boolean, cycle: Float) {
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
                style = typography.subtitle1,
                fontSize = 54.sp,
                color = Color.White
            )
            Text(
                text = (
                        if(mode) "Ciclo ${cycle.toInt()}: Trabalho" else "Ciclo ${cycle.toInt()}: Descanso"
                        ),
                style = typography.subtitle2,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun TimerGraphic(time: Long, cycle: Float, last: Int) {
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
                style = typography.subtitle1,
                fontSize = 54.sp,
                color = Color.White
            )
            Text(
                text = (
                        if(last == 2) "Ciclo ${cycle.nextDown().toInt()}: Descanso"
                        else if(last == 0) "Descanso maior"
                        else "Ciclo ${cycle.nextDown().toInt()}: Trabalho"
                        ),
                style = typography.subtitle2,
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun Chrono(
    seconds: Int,
    primaryChrono: Boolean,
    modifier: Modifier = Modifier
) {
    val nbMarker = 60
    val markerActives by animateFloatAsState(
        targetValue = nbMarker / 60f * seconds,
    )
    Box(
        modifier
            .fillMaxHeight()
            .aspectRatio(1f)
    ) {
        for (i in 0 until nbMarker) {
            Marker(
                angle = i * (360 / nbMarker),
                active = i < markerActives,
                majorMarker = i % 15 == 0,
                primaryChrono = primaryChrono
            )
        }
    }
}

@Composable
internal fun Marker(
    angle: Int,
    active: Boolean,
    majorMarker: Boolean,
    primaryChrono: Boolean,
    modifier: Modifier = Modifier
) {
    val color: Color
    val startFloatingPoint: Float
    val endFloatingPoint: Float
    val strokeWidth: Float
    if (primaryChrono) {
        startFloatingPoint = .72f
        if (majorMarker) {
            strokeWidth = 16f
            color = secondary
            endFloatingPoint = .90f
        } else {
            color = primary
            endFloatingPoint = .88f
            strokeWidth = 8f
        }
    } else {
        color = accent
        startFloatingPoint = .66f
        endFloatingPoint = .68f
        strokeWidth = 14f
    }

    Box(
        modifier
            .fillMaxSize()
            .drawBehind {
                val theta = (angle - 90) * PI.toFloat() / 180f
                val startRadius = size.width / 2 * startFloatingPoint
                val endRadius = size.width / 2 * endFloatingPoint
                val startPos = Offset(cos(theta) * startRadius, sin(theta) * startRadius)
                val endPos = Offset(cos(theta) * endRadius, sin(theta) * endRadius)
                drawLine(
                    color = if (active) color else inactiveMark,
                    start = center + startPos,
                    end = center + endPos,
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }
    )
}

@Preview
@Composable
fun DigitalTimePreview() {
    TimerGraphic(160, 2f, 1)
}