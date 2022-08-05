package com.example.organizatudo.homeContent

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.organizatudo.R
import com.example.organizatudo.navigation.NavigationItemHome
import com.example.organizatudo.ui.theme.Quicksand
import java.util.*

@Composable
fun PlayButton(
    color: Color = Color(0xFFFFFFFF),
    navController: NavController,
    router: NavigationItemHome
) {

    IconButton(onClick = {
        navController.navigate(router.route)
    }
    ) {
        Icon(
            tint = color,
            painter = painterResource(id = R.drawable.ic_play),
            contentDescription = "Play Button"
        )
    }
}

@Composable
fun RoutineBox(item : NavigationItemHome, navController: NavController) {
    var expanded by remember { mutableStateOf(false)}
    Card(
        elevation = 3.dp,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .padding(7.dp)
            .clickable { expanded = !expanded },
        backgroundColor = colorResource(id = R.color.gray_200)
        ) {
        Box(contentAlignment = Alignment.CenterEnd,
            modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = item.routineItem.img),
                contentDescription = item.routineItem.contentDesc,
                modifier = Modifier
                    .size(100.dp)
                    .offset(x = 0.dp, y = (-50).dp)
                    .clip(CircleShape))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Column(modifier = Modifier.align(Alignment.CenterVertically)) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    elevation = 4.dp,
                    color = colorResource(id = R.color.gray_200)
                ) {
                    Text(
                        text = item.routineItem.name,
                        style = MaterialTheme.typography.h5,
                        color = Color.White,
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center,
                    )
                }
                AnimatedVisibility(expanded) {
                    Column {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            elevation = 4.dp,
                            color = colorResource(id = R.color.gray_100)
                        ) {
                            Text(
                                text = item.routineItem.subtitle,
                                color = Color.White,
                                fontFamily = Quicksand,
                                style = MaterialTheme.typography.body2
                            )
                        }
                        Row {
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(32.dp),
                                color = Color(0x77000000)
                            ) {
                                PlayButton(
                                    navController = navController,
                                    router = item)
                            }

                        }
                    }
                    
                }
            }
        }
    }
}

@Composable
fun RoutineBoxPrev() {

    val navController = rememberNavController()
    Column(modifier = Modifier
        .wrapContentSize()
        .fillMaxWidth()
        .fillMaxSize()
    ) {
        RoutineBox(item = NavigationItemHome.Pomodoro, navController)
    }
}

@Preview
@Composable
fun RoutineBoxPreview() {
    RoutineBoxPrev()
}

@Composable
fun timePick(
    label: String
): MutableState<String> {
    // Fetching local context
    val mContext = LocalContext.current
    // Declaring and initializing a calendar
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    // Value for storing time as a string
    val mTime = remember { mutableStateOf("") }

    // Creating a TimePicker dialod
    val mTimePickerDialog = TimePickerDialog(
        mContext,
        {_, mHour : Int, mMinute: Int ->
            mTime.value = "${mHour.toString().padStart(2, '0')}:${mMinute.toString().padStart(2, '0')}"
        }, hour, minute, false
    )

    Column(modifier = Modifier.wrapContentSize().fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        // On button click, TimePicker is
        // displayed, user can select a time
        Button(onClick = { mTimePickerDialog.show() }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200))
        ) {
            Text(text = (
                    if(mTime.value != ""){
                        "Hora selecionada: ${mTime.value}"
                    }else{
                        label
                    }
                    ), fontSize = 14.sp,
                textAlign = TextAlign.Center, color = Color.White, fontFamily = Quicksand)
        }
    }
    return mTime
}

@Preview
@Composable
fun TimePreview() {
    timePick("Selecionar hora")
}