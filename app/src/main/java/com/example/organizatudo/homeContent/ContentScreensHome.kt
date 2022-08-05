package com.example.organizatudo.homeContent

import android.app.AlarmManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.organizatudo.R
import com.example.organizatudo.database.projects.ProjectWithDaysAndActivities
import com.example.organizatudo.datePick
import com.example.organizatudo.navigation.NavigationItem
import com.example.organizatudo.navigation.NavigationItemHome
import com.example.organizatudo.notifications.setReminder
import com.example.organizatudo.timeManagers.ChronometerApp
import com.example.organizatudo.timeManagers.StopWatchApp
import com.example.organizatudo.timeManagers.TimerApp
import com.example.organizatudo.ui.factory.ProjectViewModelFactory
import com.example.organizatudo.ui.items.ActivityItem
import com.example.organizatudo.ui.theme.Quicksand
import com.example.organizatudo.ui.viewmodel.ChronometerViewModel
import com.example.organizatudo.ui.viewmodel.InTimeViewModel
import com.example.organizatudo.ui.viewmodel.ProjectViewModel
import com.example.organizatudo.ui.viewmodel.StopWatchViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*


@Composable
fun AddProject(navController: NavController, projectViewModelFactory: ProjectViewModelFactory,
               ProjectViewModel: ProjectViewModel = viewModel(factory = projectViewModelFactory)
) {
    val context = LocalContext.current
    val scope = CoroutineScope(Dispatchers.IO)

    //main label
    val projectText : MutableState<String> = remember { mutableStateOf("")}
    val descriptionText : MutableState<String> = remember { mutableStateOf("")}

    //activities
    var dateStart: MutableState<String>
    var dateEnd: MutableState<String>
    val days = remember { mutableStateListOf<String>() }

    val activities = remember { mutableStateListOf<ActivityItem>() }

    val doubleDayDmonth: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val doubleDayUmonth: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
    val unicDayDmonth: DateTimeFormatter = DateTimeFormatter.ofPattern("d/MM/yyyy")
    val unicDayUmonth: DateTimeFormatter = DateTimeFormatter.ofPattern("d/M/yyyy")

    var showAct by remember { mutableStateOf(false)}


    LazyColumn(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.gray_200)),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        item {
            Card(elevation = 3.dp,
                shape = RoundedCornerShape(0.dp),
                backgroundColor = colorResource(id = R.color.gray_200)
            ) {
                Column(modifier = Modifier.padding(4.dp)
                ) {
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = projectText.value,
                        onValueChange = {
                            projectText.value = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        label = { Text(text = "Nome do projeto", style = MaterialTheme.typography.caption,
                            color = Color.White, fontFamily = Quicksand) },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Quicksand
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.teal_200),
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    OutlinedTextField(
                        value = descriptionText.value,
                        onValueChange = {
                            descriptionText.value = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.None,
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text
                        ),
                        singleLine = true,
                        label = { Text(text = "Descrição", style = MaterialTheme.typography.caption,
                            color = Color.White, fontFamily = Quicksand) },
                        textStyle = TextStyle(
                            color = Color.White,
                            fontSize = 14.sp,
                            fontFamily = Quicksand
                        ),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = colorResource(id = R.color.teal_200),
                            unfocusedBorderColor = Color.White,
                            cursorColor = Color.White)
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    dateStart = datePick(label = "selecionar data de início")
                    Spacer(modifier = Modifier.height(5.dp))
                    dateEnd = datePick(label = "selecionar data de término")
                    Spacer(modifier = Modifier.height(5.dp))
                    if(dateStart.value.isNotEmpty() && dateEnd.value.isNotEmpty()) {
                        days.clear()
                        var start = if(dateStart.value[1] == '/') {
                            if(dateStart.value[3] == '/') {
                                LocalDate.parse(dateStart.value, unicDayUmonth)
                            } else LocalDate.parse(dateStart.value, unicDayDmonth)
                        } else {
                            if(dateStart.value[4] == '/') {
                                LocalDate.parse(dateStart.value, doubleDayUmonth)
                            } else LocalDate.parse(dateStart.value, doubleDayDmonth)
                        }

                        val end = if(dateEnd.value[1] == '/') {
                            if(dateEnd.value[3] == '/') {
                                LocalDate.parse(dateEnd.value, unicDayUmonth)
                            } else LocalDate.parse(dateEnd.value, unicDayDmonth)
                        } else {
                            if(dateEnd.value[4] == '/') {
                                LocalDate.parse(dateEnd.value, doubleDayUmonth)
                            } else LocalDate.parse(dateEnd.value, doubleDayDmonth)
                        }

                        while(start.isBefore(end.plusDays(1L))) {
                            days.add(start.toString())
                            start = start.plusDays(1L)
                        }
                    }
                    AnimatedVisibility(visible = (dateStart.value != "" && dateEnd.value != "")) {
                        Card(elevation = 0.dp,
                            shape = RoundedCornerShape(0.dp),
                            backgroundColor = colorResource(id = R.color.teal_700),
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showAct = ! showAct }
                            ) {
                                Spacer(modifier = Modifier.weight(0.1f))
                                Text(text = "Atividades", style = MaterialTheme.typography.subtitle1,
                                    fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.Black)
                                Spacer(modifier = Modifier.weight(0.4f))
                                IconButton(onClick = { showAct = ! showAct }
                                ) {
                                    Icon(imageVector = (
                                            if (!showAct) Icons.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown),
                                        contentDescription = "expanded activity")
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }
        items(items = activities) { item ->
            AnimatedVisibility(showAct) {
                Column {
                    Box(modifier = Modifier.fillParentMaxWidth()) {
                        Surface (shape = RoundedCornerShape(5.dp),
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .padding(3.dp),
                            color = Color(0x77000000)
                        ) {
                            Column {
                                OutlinedTextField(value = item.name.value,
                                    onValueChange = { item.name.value = it},
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(
                                        capitalization = KeyboardCapitalization.None,
                                        autoCorrect = true,
                                        keyboardType = KeyboardType.Text
                                    ),
                                    singleLine = true,
                                    label = { Text(text = "Nome da atividade", style = MaterialTheme.typography.caption,
                                        color = Color.White, fontFamily = Quicksand) },
                                    textStyle = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontFamily = Quicksand
                                    ),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = colorResource(id = R.color.teal_200),
                                        unfocusedBorderColor = Color.White,
                                        cursorColor = Color.White)
                                )
                                Spacer(modifier = Modifier.height(5.dp))
                                item.start = timePick("Selecionar hora de início")
                                Spacer(modifier = Modifier.height(5.dp))
                                item.end = timePick("Selecionar hora de término")
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                        }
                    }
                }
            }
        }
        item {
            AnimatedVisibility(showAct) {
                Surface (shape = RoundedCornerShape(5.dp),
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(3.dp),
                    color = Color(0x77000000)
                ) {
                    Row {
                        Spacer(modifier = Modifier.weight(0.95f))
                        IconButton(
                            enabled = !activities.isEmpty(),
                            onClick = {
                                activities.removeAt(activities.lastIndex)
                                Toast.makeText(context, "Atividade removida", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "remover atividade", tint = Color.White)
                        }
                        IconButton(
                            onClick = {
                                activities.add(ActivityItem(mutableStateOf(""), mutableStateOf(""), mutableStateOf("")))
                                Toast.makeText(context, "Atividade adicionada", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "adicinar atividade", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(0.05f))
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(5.dp))
            Button(modifier = Modifier
                .wrapContentSize()
                .fillMaxWidth(),
                onClick = {

                    scope.launch {
                        val projectId = ProjectViewModel.addProject(
                            projectText.value,
                            descriptionText.value,
                            days.size.toString(),
                            Calendar.getInstance().time.toString(),
                            Calendar.getInstance().time.toString(),
                            "00:00:00"
                        )
                        for(i in 0 until days.size) {
                            ProjectViewModel.addDay(projectId.toInt(), activities.size.toString())
                        }
                        for(i in 0 until activities.size) {
                            ProjectViewModel.addActivity(projectId.toInt(), activities[i].name.value, activities[i].start.value, activities[i].end.value)
                            val strt = LocalTime.parse(activities[i].start.value + ":00.00")
                            val time = Calendar.getInstance()
                            time.set(Calendar.HOUR_OF_DAY, strt.hour)
                            time.set(Calendar.MINUTE, strt.minute)
                            time.set(Calendar.SECOND, strt.second)
                            for(k in 0 until days.size) {
                                setReminder(context, time.timeInMillis + (k * AlarmManager.INTERVAL_DAY), activities[i].name.value, "sua atividade começa às ${activities[i].start.value}", alarm = R.raw.alarm14)
                            }
                        }
                    }
                    Toast.makeText(context, "Projeto ${projectText.value} adicionado", Toast.LENGTH_SHORT).show()
                    navController.navigate(NavigationItem.Home.route)
                }, enabled = projectText.value.isNotEmpty() && descriptionText.value.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_700))
            ) {
                Text(text = "Adicionar projeto", textAlign = TextAlign.Center,
                    color = Color.White, fontFamily = Quicksand)
            }
        }
    }
}

@Composable
fun ProjectScreen(projectViewModelFactory: ProjectViewModelFactory,
                  projectId: Int,
                  ProjectViewModel: ProjectViewModel = viewModel(factory = projectViewModelFactory),
) {
    var showAct by remember { mutableStateOf(false)}
    val all = ProjectViewModel.projectsList.collectAsState(initial = emptyList())
    val content : MutableList<ProjectWithDaysAndActivities> = mutableListOf()
    var clicked by remember { mutableStateOf(false) }
    Surface {
        all.value.forEach {
            if(it.project.id == projectId){
                content.add(it)
            }
        }
        if(content.isNotEmpty()) {
            LazyColumn(horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.gray_200)),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                item {
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Column {
                            Text(text = "Nome: ${content[0].project.name}", style = MaterialTheme.typography.subtitle1,
                                color = Color.White, fontFamily = Quicksand)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Descrição:\n${content[0].project.description}", style = MaterialTheme.typography.body1,
                                color = Color.White, fontFamily = Quicksand)
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(text = "Data de criação: ${content[0].project.creationDate}", style = MaterialTheme.typography.body1,
                                fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.White)
                        }
                    }
                }
                item {
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Column(modifier = Modifier.padding(4.dp)
                        ) {
                            Card(elevation = 0.dp,
                                shape = RoundedCornerShape(0.dp),
                                backgroundColor = colorResource(id = R.color.teal_200),
                                modifier = Modifier
                                    .wrapContentSize()
                                    .fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { showAct = !showAct }
                                ) {
                                    Spacer(modifier = Modifier.weight(0.1f))
                                    Text(text = "Atividades", style = MaterialTheme.typography.subtitle1,
                                        fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.Black)
                                    Spacer(modifier = Modifier.weight(0.4f))
                                    IconButton(onClick = {
                                        showAct = !showAct

                                        }
                                    ) {
                                        Icon(imageVector = (
                                                if (!showAct) Icons.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown),
                                            contentDescription = "expanded activity")
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
                    items(items = content[0].CompDays) { item ->
                        AnimatedVisibility(showAct) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.fillParentMaxWidth()) {
                                    Surface(
                                        shape = RoundedCornerShape(5.dp),
                                        modifier = Modifier
                                            .wrapContentSize()
                                            .fillMaxWidth()
                                            .padding(3.dp),
                                        color = Color(0x77000000)
                                    ) {
                                       if(item.days.isNotEmpty()) {
                                           Column {
                                               item.days.forEach {
                                                   Card(elevation = 3.dp,
                                                       shape = RoundedCornerShape(0.dp),
                                                       backgroundColor = colorResource(id = R.color.gray_100),
                                                       modifier = Modifier
                                                           .fillMaxWidth()
                                                           .padding(2.dp)
                                                   ) {
                                                       Column(modifier = Modifier.padding(5.dp)) {
                                                           Text(text = "Nome da Atividade: ${it.activity}", style = MaterialTheme.typography.body2,
                                                               fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.White)
                                                           Spacer(modifier = Modifier.height(5.dp))
                                                           Text(text = "Horário de início: ${it.start}", style = MaterialTheme.typography.body2,
                                                               fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.White)
                                                           Spacer(modifier = Modifier.height(5.dp))
                                                           Text(text = "Horário de término: ${it.end}", style = MaterialTheme.typography.body2,
                                                               fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.White)
                                                           Spacer(modifier = Modifier.height(5.dp))
                                                       }
                                                   }
                                               }
                                           }
                                       }
                                       else {
                                           Column(verticalArrangement = Arrangement.Center) {
                                               Button(onClick = { clicked = !clicked },
                                                   colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_700))
                                               ) {
                                                   Text(text = "Carregar dados",
                                                       style = MaterialTheme.typography.subtitle1,
                                                       fontFamily = Quicksand,
                                                       fontWeight = FontWeight.SemiBold,
                                                       color = Color.White,
                                                       textAlign = TextAlign.Center)
                                               }
                                           }
                                       }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.Center) {
                Button(onClick = { clicked = !clicked },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_700))
                ) {
                    Text(text = "Carregar dados",
                        style = MaterialTheme.typography.subtitle1,
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun PomodoroScreen(navController: NavController, router: NavigationItemHome) {

    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    var workTime by remember { mutableStateOf(25) }
    var freeTime by remember { mutableStateOf(5) }
    var longFreeTime by remember { mutableStateOf(20)}
    var work by remember { mutableStateOf("")}
    var free by remember { mutableStateOf("")}
    var longFree by remember { mutableStateOf("")}

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700)),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        item {
            Card(elevation = 3.dp,
                modifier = Modifier
                    .clickable { expanded = !expanded },
                backgroundColor = colorResource(id = R.color.gray_200)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(7.dp)
                ) {
                    Text(
                        text = "1. Escolha uma tarefa que você precisa fazer. \n\n" +
                                "2. Ponha um timer (ex., por 25 minutos).\n\n" +
                                "3. Concentre-se na tarefa em frente.\n\n" +
                                "4. Quando o alarme soar, ponha um marcador onde você parou\n\n" +
                                "5. Descanse um pouco: Caminhe, pegue uma xícara de café, faça algo que não esteja relacionado ao trabalho pra descansar seu cérebro.\n\n" +
                                "6. Repita as etapas 2 a 5: Quando terminar o processo 4 vezes, você pode começar a descansar mais (20–30 mins).\n\n" +
                                " Os valores recomendados são:\nTrabalho: 25 minutos\nDescanso: 5 minutos",
                        style = MaterialTheme.typography.body1,
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp)
                            .wrapContentSize(),
                        color = Color(0x77000000)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(4.dp)
                        ) {
                            Column(modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Spacer(modifier = Modifier.height(5.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(4.dp)
                                ) {
                                    OutlinedTextField(value = work,
                                        onValueChange = { work = it},
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.None,
                                            autoCorrect = true,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        singleLine = true,
                                        label = { Text(text = "Trabalho", style = MaterialTheme.typography.caption,
                                            color = Color.White, fontFamily = Quicksand) },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = Quicksand
                                        ),colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = colorResource(id = R.color.teal_200),
                                            unfocusedBorderColor = Color.White,
                                            cursorColor = Color.White
                                        )
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(4.dp)
                                ) {
                                    OutlinedTextField(value = free,
                                        onValueChange = { free = it},
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.None,
                                            autoCorrect = true,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        singleLine = true,
                                        label = { Text(text = "Descanso", style = MaterialTheme.typography.caption,
                                            color = Color.White, fontFamily = Quicksand) },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = Quicksand
                                        ),colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = colorResource(id = R.color.teal_200),
                                            unfocusedBorderColor = Color.White,
                                            cursorColor = Color.White
                                        )
                                    )
                                }
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(4.dp)
                                ) {
                                    OutlinedTextField(value = longFree,
                                        onValueChange = { longFree = it},
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.None,
                                            autoCorrect = true,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        singleLine = true,
                                        label = { Text(text = "Descanso maior", style = MaterialTheme.typography.caption,
                                            color = Color.White, fontFamily = Quicksand) },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = Quicksand
                                        ),colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = colorResource(id = R.color.teal_200),
                                            unfocusedBorderColor = Color.White,
                                            cursorColor = Color.White
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                            }
                            Text(text = "Os valores são em minutos",
                                style = MaterialTheme.typography.subtitle1,
                                fontFamily = Quicksand,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.size(10.dp))
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp)
                            .size(64.dp),
                        color = Color(0x77000000)
                    ) {
                        IconButton(onClick = {
                            workTime = work.toInt()
                            freeTime = free.toInt()
                            longFreeTime = longFree.toInt()
                            navController.navigate(router.routeActivity +
                                    "/" + workTime +
                                    "/" + freeTime +
                                    "/" + longFreeTime)
                        }, enabled = work.isNotEmpty() && free.isNotEmpty() && longFree.isNotEmpty()
                        ) {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = "Play Button"
                            )
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun PomodoroActivityScreen(workTime: Long, freeTime: Long, longFreeTime: Long, viewModel: StopWatchViewModel) {
    StopWatchApp(viewModel, workTime = workTime * 60_000, freeTime = freeTime * 60_000, longFreeTime = longFreeTime * 60_000)
}


@Composable
fun FlowTimeScreen(navController: NavController, router: NavigationItemHome) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700)),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        item {
            Card(elevation = 3.dp,
                modifier = Modifier
                    .clickable { expanded = !expanded },
                backgroundColor = colorResource(id = R.color.gray_200)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(7.dp)
                ) {
                    Text(
                        text = "1. Escolha uma tarefa que você precisa fazer. \n\n" +
                                "2. Dispare o cronômetro e começe a trabalhar.\n\n" +
                                "3. Concentre-se na tarefa em frente.\n\n" +
                                "4. Quando terminar a tarefa ou se sentir cansado, pare o cronômetro.\n\n" +
                                "5. Calcularemos o seu tempo de descanso com base no tempo trabalhado.\n\n" +
                                "6. Descanse um pouco: Caminhe, pegue uma xícara de café, faça algo que não esteja relacionado ao trabalho pra descansar seu cérebro.\n\n" +
                                "7. Repita as etapas 2 a 6 enquanto achar necessário..\n\n" +
                                "Valores de descanso:\n\n" +
                                "0-25 minutos de trabalho: 5 minutos.\n\n" +
                                "25-50 minutos de trabalho: 8 minutos.\n\n" +
                                "50-90 minutos de trabalho: 10 minutos.\n\n" +
                                "90+ minutos de trabalho: 15 minutos.",
                        style = MaterialTheme.typography.body1,
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp)
                            .size(64.dp),
                        color = Color(0x77000000)
                    ) {
                        IconButton(onClick = {
                            navController.navigate(router.routeActivity)
                        }
                        ) {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = "Play Button"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowTimeActivityScreen(viewModel: ChronometerViewModel) {
    ChronometerApp(viewModel = viewModel)
}

@Composable
fun JustInTimeScreen(navController: NavController, router: NavigationItemHome) {
    var expanded by remember { mutableStateOf(false) }
    val scrollState = rememberLazyListState()
    var workTime by remember { mutableStateOf(1)}
    var work by remember { mutableStateOf("")}

    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .wrapContentSize()
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700)),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        item {
            Card(elevation = 3.dp,
                modifier = Modifier
                    .clickable { expanded = !expanded },
                backgroundColor = colorResource(id = R.color.gray_200)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(7.dp)
                ) {
                    Text(
                        text = "1. Escolha uma tarefa que você precisa fazer. \n\n" +
                                "2. Coloque um tempo limite de timer.\n\n" +
                                "3. Concentre-se na tarefa em frente.\n\n" +
                                "4. O desafio para quando você terminar a tarefa ou o tempo acabar, o que vier primeiro.\n\n" +
                                "5. Descanse um pouco: Caminhe, pegue uma xícara de café, faça algo que não esteja relacionado ao trabalho pra descansar seu cérebro.\n\n" +
                                "6. Repita as etapas 1 a 6 enquanto achar necessário.\n\n",
                        style = MaterialTheme.typography.body1,
                        fontFamily = Quicksand,
                        fontWeight = FontWeight.Normal,
                        color = Color.White,
                        textAlign = TextAlign.Start
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Surface(
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp)
                            .wrapContentSize(),
                        color = Color(0x77000000)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            horizontalAlignment = Alignment.End,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(4.dp)
                        ) {
                            Row(modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    horizontalAlignment = Alignment.Start,
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .padding(4.dp)
                                ) {
                                    OutlinedTextField(value = work,
                                        onValueChange = { work = it},
                                        modifier = Modifier.fillMaxWidth(),
                                        keyboardOptions = KeyboardOptions(
                                            capitalization = KeyboardCapitalization.None,
                                            autoCorrect = true,
                                            keyboardType = KeyboardType.Number
                                        ),
                                        singleLine = true,
                                        label = { Text(text = "Tempo limite", style = MaterialTheme.typography.caption,
                                            color = Color.White, fontFamily = Quicksand) },
                                        textStyle = TextStyle(
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontFamily = Quicksand
                                        ),colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = colorResource(id = R.color.teal_200),
                                            unfocusedBorderColor = Color.White,
                                            cursorColor = Color.White
                                        )
                                    )
                                }
                            }
                            Text(text = "Os valores são em minutos",
                                style = MaterialTheme.typography.subtitle1,
                                fontFamily = Quicksand,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.size(5.dp))
                    Surface(
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(6.dp)
                            .size(64.dp),
                        color = Color(0x77000000)
                    ) {
                        IconButton(onClick = {
                            workTime = work.toInt()
                            navController.navigate(router.routeActivity + "/$workTime")
                        }, enabled = work.isNotEmpty()
                        ) {
                            Icon(
                                tint = Color.White,
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = "Play Button"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JustInTimeActivityScreen(viewModel: InTimeViewModel, workTime: Long) {
    TimerApp(viewModel, workTime * 60_000)
}