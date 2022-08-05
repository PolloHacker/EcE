package com.example.organizatudo

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.organizatudo.database.todoList.TodoTable
import com.example.organizatudo.homeContent.RoutineBox
import com.example.organizatudo.homeContent.timePick
import com.example.organizatudo.navigation.NavigationItemDB
import com.example.organizatudo.navigation.NavigationItemHome
import com.example.organizatudo.notifications.setReminder
import com.example.organizatudo.ui.factory.ProjectViewModelFactory
import com.example.organizatudo.ui.factory.TodoItemViewModelFactory
import com.example.organizatudo.ui.items.Subjects
import com.example.organizatudo.ui.items.deserialize
import com.example.organizatudo.ui.items.serialize
import com.example.organizatudo.ui.theme.Quicksand
import com.example.organizatudo.ui.viewmodel.ProjectViewModel
import com.example.organizatudo.ui.viewmodel.TodoItemViewModel
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.SelectionMode
import io.github.boguszpawlowski.composecalendar.selection.SelectionState
import me.bytebeats.views.charts.line.LineChart
import me.bytebeats.views.charts.line.LineChartData
import me.bytebeats.views.charts.line.LineChartData.Point
import me.bytebeats.views.charts.line.render.line.SolidLineDrawer
import me.bytebeats.views.charts.line.render.point.FilledCircularPointDrawer
import me.bytebeats.views.charts.line.render.xaxis.SimpleXAxisDrawer
import me.bytebeats.views.charts.line.render.yaxis.SimpleYAxisDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun HomeScreen(navController: NavController, todoItemViewModelFactory: TodoItemViewModelFactory,
               projectViewModelFactory: ProjectViewModelFactory,
               ProjectViewModel: ProjectViewModel = viewModel(factory = projectViewModelFactory),
               TodoViewModel: TodoItemViewModel = viewModel(factory = todoItemViewModelFactory)
               ) {
    val methods = listOf(
        NavigationItemHome.Pomodoro,
        NavigationItemHome.FlowTime,
        NavigationItemHome.JustInTime

    )
    val itemsList = TodoViewModel.itemList.collectAsState(initial = emptyList())
    val projects = ProjectViewModel.projectsList.collectAsState(initial = emptyList())
    val scrollState = rememberLazyListState()
    var dialogState by remember { mutableStateOf(false) }

    var showMethods by remember { mutableStateOf(true)}
    var showEv by remember { mutableStateOf(false)}
    var showProjects by remember { mutableStateOf(false) }

    LazyColumn(
        state = scrollState,
        contentPadding = PaddingValues(horizontal = 1.dp, vertical = 1.dp),
        modifier = Modifier
            .wrapContentSize()
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700)),
        verticalArrangement = Arrangement.spacedBy(10.dp)

    ) {
        item {
            Card(elevation = 10.dp,
                shape = RoundedCornerShape(0.dp),
                backgroundColor = colorResource(id = R.color.teal_200),
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showMethods = ! showMethods }
                    ) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(text = "Trabalho Livre", style = MaterialTheme.typography.subtitle1,
                        fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.Black)
                    Spacer(modifier = Modifier.weight(0.4f))
                    IconButton(onClick = { showMethods = ! showMethods }) {
                        Icon(
                            imageVector = (
                                    if (!showMethods) Icons.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown
                                    ), contentDescription = "expanded events"
                        )
                    }
                }
            }
        }
        items(methods) { item ->
            AnimatedVisibility(showMethods) {
                RoutineBox(item = item, navController)
            }
        }
        item {
            Card(elevation = 10.dp,
                shape = RoundedCornerShape(0.dp),
                backgroundColor = colorResource(id = R.color.teal_200),
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showProjects = ! showProjects }) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(text = "Projetos", style = MaterialTheme.typography.subtitle1,
                        fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.Black)
                    Spacer(modifier = Modifier.weight(0.4f))
                    IconButton(onClick = { showProjects = ! showProjects }) {
                        Icon(imageVector = (
                                if (!showProjects) Icons.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown
                                ), contentDescription = "expanded projects")
                    }
                }
            }
        }
        items(items = projects.value, key = {item -> item.project.id}) { item ->
            AnimatedVisibility(showProjects) {
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
                            painter = painterResource(id = R.drawable.ic_api_card),
                            contentDescription = "Project card",
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
                                    text = item.project.name,
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
                                            text = item.project.description,
                                            color = Color.White,
                                            fontFamily = Quicksand,
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                    Row {
                                        Spacer(modifier = Modifier.weight(0.95f))
                                        Surface(
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(32.dp),
                                            color = Color(0x77000000)
                                        ) {
                                            IconButton(onClick = { dialogState = !dialogState }
                                            ) {
                                                Icon(
                                                    tint = Color(0xFFFFFFFF),
                                                    imageVector = Icons.Rounded.Delete,
                                                    contentDescription = "Delete Project")
                                                if(dialogState) {
                                                    RemoveDialog(show = dialogState, onDismiss = { dialogState = false },
                                                        onConfirm = {
                                                            ProjectViewModel.removeProject(item.project)
                                                            ProjectViewModel.removeDay(item.project.id)
                                                            ProjectViewModel.removeActivity(item.project.id)
                                                            dialogState = false
                                                                    }, "Remover Projeto?", "\nEsta ação é irreversível")

                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.weight(0.05f))
                                        Surface(
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .padding(6.dp)
                                                .size(32.dp),
                                            color = Color(0x77000000)
                                        ) {
                                            IconButton(onClick = {
                                                navController.navigate(NavigationItemDB.ProjectScreen.route + "/" + item.project.id)
                                            }
                                            ) {
                                                Icon(
                                                    tint = Color(0xFFFFFFFF),
                                                    painter = painterResource(id = R.drawable.ic_play),
                                                    contentDescription = "Play Button")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        item {
            AnimatedVisibility(showProjects) {
                Card(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .padding(7.dp),
                    backgroundColor = colorResource(id = R.color.gray_200)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Spacer(modifier = Modifier.weight(0.3f))
                        Text(text = "Adicionar Projeto", style = MaterialTheme.typography.button, fontFamily = Quicksand,
                            textAlign = TextAlign.Center,
                            color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                        Surface(
                            shape = CircleShape,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(32.dp),
                            color = Color(0x77000000)
                        ) {
                            Row {
                                Spacer(modifier = Modifier.weight(0.65f))
                                IconButton(onClick = {
                                    navController.navigate(NavigationItemDB.AddProject.route)
                                }
                                ) {
                                    Icon(
                                        tint = Color(0xFFFFFFFF),
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
        item {
            Card(elevation = 10.dp,
                shape = RoundedCornerShape(0.dp),
                backgroundColor = colorResource(id = R.color.teal_200),
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showEv = ! showEv }
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Text(text = "Eventos", style = MaterialTheme.typography.subtitle1,
                        fontFamily = Quicksand, textAlign = TextAlign.Start, color = Color.Black)
                    Spacer(modifier = Modifier.weight(0.4f))
                    IconButton(onClick = { showEv = ! showEv }) {
                        Icon(
                            imageVector = (
                                    if (!showEv) Icons.Rounded.KeyboardArrowRight else Icons.Rounded.KeyboardArrowDown
                                    ), contentDescription = "expanded events"
                        )
                    }
                }
            }
        }
        itemsIndexed(items = itemsList.value) {index,  item ->
            AnimatedVisibility(showEv) {
                Card(elevation = 3.dp,
                    shape = RoundedCornerShape(0.dp),
                    backgroundColor = colorResource(id = R.color.gray_200)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .wrapContentSize()
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        EventRow(item, todoItemViewModelFactory, index = index + 1)
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarScreen(factory: TodoItemViewModelFactory,
                   TodoViewModel: TodoItemViewModel = viewModel(factory = factory)
) {
    val scrollState = rememberLazyListState()
    val context = LocalContext.current
    val calendarState = rememberSelectableCalendarState(
        initialMonth = YearMonth.now(),
        initialSelectionMode = SelectionMode.Single,
    )
    val selectionState = calendarState.selectionState
    val itemsToday = TodoViewModel.itemList.collectAsState(initial = emptyList())
    val itemsInDay: MutableList<TodoTable> = mutableListOf()

    val eventText : MutableState<String> = remember { mutableStateOf("")}
    val descriptionText : MutableState<String> = remember { mutableStateOf("")}
    var time: MutableState<String>

    var add by remember { mutableStateOf(false)}
    var isSelected by remember { mutableStateOf(false) }
    isSelected = selectionState.selection.joinToString { it.toString() } != ""

    Surface {
        itemsToday.value.forEach {
            if(selectionState.selectionMode.name == "Single" && selectionState.selection.lastIndex == 0) {
                if(selectionState.selection[0].toString() == it.exp_date || selectionState.selection[0].toString() == it.start_date) {
                    itemsInDay.add(it)
                }
            }
        }
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(horizontal = 1.dp, vertical = 1.dp),
            modifier = Modifier
                .wrapContentSize()
                .fillMaxSize()
                .background(colorResource(id = R.color.teal_700))

        ) {
            item {
                Card(elevation = 3.dp,
                    shape = RoundedCornerShape(0.dp),
                    backgroundColor = colorResource(id = R.color.gray_200)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .wrapContentSize()
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        SelectableCalendar(
                            calendarState = calendarState,
                            showAdjacentMonths = false,
                            monthHeader = { MonthHeader(monthState = it) },
                            weekHeader = { WeekHeader(daysOfWeek = it) },
                            dayContent = { DefaultDay(state = it, factory = factory, TodoViewModel = TodoViewModel) },
                            monthContainer = { MonthContainer(it) })
                        Text(
                            text = "Modo de selecão do calendário",
                            style = MaterialTheme.typography.body1,
                            fontFamily = Quicksand,
                            color = Color.White
                        )
                        Row(modifier = Modifier.fillMaxWidth()) {
                            SelectionMode.values().forEach { selectionMode ->
                                Row(modifier = Modifier.clickable {  selectionState.selectionMode = selectionMode }) {
                                    RadioButton(
                                        selected = selectionState.selectionMode == selectionMode,
                                        onClick = {
                                            selectionState.selectionMode = selectionMode
                                        }
                                    )
                                    Text(text = (
                                            when (selectionMode.name) {
                                                "None" -> {
                                                    "Nulo"
                                                }
                                                "Single" -> {
                                                    "Único"
                                                }
                                                "Multiple" -> {
                                                    "Múltiplo"
                                                }
                                                "Period" -> {
                                                    "Período"
                                                }
                                                else -> {
                                                    "Indefinido"
                                                }
                                            }), style = MaterialTheme.typography.caption, fontFamily = Quicksand,
                                        textAlign = TextAlign.Start,
                                        color = Color.White, modifier = Modifier.align(Alignment.CenterVertically))
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        Text(text = "A lista de tarefas é atualizada automaticamente",
                            style = MaterialTheme.typography.subtitle2,
                            fontFamily = Quicksand,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center)
                    }
                }
            }
            item {
                AnimatedVisibility(
                    (selectionState.selectionMode.toString() == "Single" && isSelected)
                ) {
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            AnimatedVisibility(isSelected) {

                                Surface(shape = RoundedCornerShape(5.dp),
                                    modifier = Modifier
                                        .wrapContentSize()
                                        .fillMaxWidth()
                                        .padding(6.dp),
                                    color = Color(0x77000000)
                                ) {
                                    Column(modifier = Modifier
                                        .wrapContentSize()
                                        .fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(text = (
                                                if(itemsInDay.lastIndex >= 0){
                                                    "Os eventos do dia ${selectionState.selection.joinToString { it.dayOfMonth.toString() + "/" + it.monthValue + "/" + it.year.toString() }} são:"
                                                } else{
                                                    "Não há nenhum evento no dia ${selectionState.selection.joinToString { it.dayOfMonth.toString() + "/" + it.monthValue + "/" + it.year.toString() }}."
                                                }
                                                ), style = MaterialTheme.typography.body1, fontFamily = Quicksand,
                                            textAlign = TextAlign.Start, color = Color.White)
                                        Spacer(modifier = Modifier.height(5.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            itemsIndexed(items = itemsInDay) {index, item ->
                AnimatedVisibility(
                    (selectionState.selectionMode.name == "Single" && isSelected)
                ){
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            EventRow(item, factory, index = index + 1)
                        }
                    }
                }
            }
            item {
                AnimatedVisibility(isSelected) {
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Row {
                                Button(onClick = {
                                    add = !add
                                }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200))) {
                                    Text(text = (if(!add) "adicionar evento" else "cancelar"), style = MaterialTheme.typography.button, textAlign = TextAlign.Center,
                                        color = Color.White, fontFamily = Quicksand)
                                }
                            }
                        }
                    }
                }
            }
            item {
                AnimatedVisibility(add) {
                    Card(elevation = 3.dp,
                        shape = RoundedCornerShape(0.dp),
                        backgroundColor = colorResource(id = R.color.gray_200)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .wrapContentSize()
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            Spacer(modifier = Modifier.height(5.dp))
                            OutlinedTextField(
                                value = eventText.value,
                                onValueChange = {
                                    eventText.value = it
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = true,
                                    keyboardType = KeyboardType.Text
                                ),
                                singleLine = true,
                                label = { Text(text = "Nome do evento", style = MaterialTheme.typography.caption,
                                    color = Color.White, fontFamily = Quicksand) },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = Quicksand
                                )
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
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = Quicksand
                                )
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(onClick = {  }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200))){
                                    Text(text = (
                                            if(selectionState.selection.lastIndex > 0){
                                                "Datas selecionadas: ${selectionState.selection.joinToString { 
                                                    it.dayOfMonth.toString().padStart(2, '0') + '/' + it.monthValue.toString().padStart(2, '0') + '/' + it.year}}"
                                            }
                                            else{
                                                "Data selecionada: ${selectionState.selection.joinToString { 
                                                    it.dayOfMonth.toString().padStart(2, '0') + '/' + it.monthValue.toString().padStart(2, '0') + '/' + it.year}}"
                                            }
                                            ), fontSize = 14.sp,
                                        textAlign = TextAlign.Center, color = Color.White, fontFamily = Quicksand)
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            time = timePick("Selecionar hora de entrega")
                            Spacer(modifier = Modifier.height(5.dp))
                            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Button(onClick = {
                                    when(selectionState.selectionMode.name) {
                                        "None" -> {
                                            //Nothing happens
                                        }
                                        "Single" -> {
                                            TodoViewModel.addItem(
                                                name = eventText.value,
                                                description = descriptionText.value,
                                                start_date = selectionState.selection[0].toString(),
                                                exp_date = selectionState.selection[selectionState.selection.size - 1].toString(),
                                                time = time.value
                                            )
                                            val strt = LocalTime.parse(time.value + ":00.00")
                                            val cal = Calendar.getInstance()
                                            cal.set(Calendar.HOUR_OF_DAY, strt.hour)
                                            cal.set(Calendar.MINUTE, strt.minute)
                                            cal.set(Calendar.SECOND, strt.second)
                                            setReminder(context, cal.timeInMillis,
                                                "Fique atento",
                                                "O evento ${eventText.value} começa às ${time.value}",
                                                alarm = R.raw.alarm14
                                                )
                                        }
                                        else -> {
                                            TodoViewModel.addItem(
                                                name = eventText.value,
                                                description = descriptionText.value,
                                                start_date = selectionState.selection[0].toString(),
                                                exp_date = selectionState.selection[selectionState.selection.size - 1].toString(),
                                                time = time.value
                                            )
                                            val strt = LocalTime.parse(time.value + ":00.00")
                                            val cal = Calendar.getInstance()
                                            cal.set(Calendar.HOUR_OF_DAY, strt.hour)
                                            cal.set(Calendar.MINUTE, strt.minute)
                                            cal.set(Calendar.SECOND, strt.second)
                                            for(k in selectionState.selection.indices) {
                                                setReminder(context, cal.timeInMillis + (k * AlarmManager.INTERVAL_DAY),
                                                    "Fique atento",
                                                    "O evento ${eventText.value} começa às $strt",
                                                    alarm = R.raw.alarm14
                                                )
                                            }
                                            Toast.makeText(context, strt.toString(), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    Toast.makeText(context, "Evento ${eventText.value} adicionado", Toast.LENGTH_SHORT).show()
                                    descriptionText.value = ""
                                    eventText.value = ""
                                    add = !add
                                }, enabled = eventText.value.isNotEmpty() && descriptionText.value.isNotEmpty() && time.value.isNotEmpty(),
                                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_700))) {
                                    Text(text = "Adicionar evento", textAlign = TextAlign.Center,
                                        color = Color.White, fontFamily = Quicksand)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun MonthContainer(content: @Composable (PaddingValues) -> Unit) {
    Card(
        elevation = 0.dp,
        shape = RoundedCornerShape(10.dp),
        backgroundColor = MaterialTheme.colors.surface,
        border = BorderStroke(1.dp, Color.LightGray),
        content = { content(PaddingValues(4.dp)) },
    )
}

@Composable
fun MonthHeader(
    monthState: MonthState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        IconButton(
            modifier = Modifier.testTag("Decrement"),
            onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowLeft,
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = "Previous",
            )
        }
        Text(
            modifier = Modifier.testTag("MonthLabel"),
            text = monthState.currentMonth.month
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                .lowercase()
                .replaceFirstChar { it.titlecase() },
            style = MaterialTheme.typography.h4,
            color = Color.White,
            fontFamily = Quicksand,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = monthState.currentMonth.year.toString(),
            style = MaterialTheme.typography.h4, fontFamily = Quicksand, color = Color.White)
        IconButton(
            modifier = Modifier.testTag("Increment"),
            onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }
        ) {
            Image(
                imageVector = Icons.Default.KeyboardArrowRight,
                colorFilter = ColorFilter.tint(Color.White),
                contentDescription = "Next",
            )
        }
    }
}

@Composable
fun WeekHeader(
    daysOfWeek: List<DayOfWeek>,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                color = Color.White,
                fontFamily = Quicksand,
                modifier = modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}

@Composable
private fun <T : SelectionState> DefaultDay(
    modifier: Modifier = Modifier,
    state: DayState<T>,
    factory: TodoItemViewModelFactory,
    TodoViewModel: TodoItemViewModel = viewModel(factory = factory),
    selectionColor: Color = colorResource(id = R.color.teal_200),
    hasItemColor: Color = colorResource(id = android.R.color.holo_red_light),
    currentDayColor: Color = MaterialTheme.colors.secondary,
    onClick: (LocalDate) -> Unit = {},
) {
    val date = state.date
    val selectionState = state.selectionState
    val items = TodoViewModel.itemList.collectAsState(initial = emptyList())
    val isSelected = selectionState.isDateSelected(date)

    Card(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp),
        elevation = if (state.isFromCurrentMonth) 4.dp else 0.dp,
        border = if (state.isCurrentDay) BorderStroke(2.dp, currentDayColor) else null,
        contentColor = (
                if (isSelected) selectionColor
                else if (inDay(items, date)) hasItemColor
                else contentColorFor(
            backgroundColor = MaterialTheme.colors.surface
        ))
    ) {
        Box(
            modifier = Modifier.clickable {
                onClick(date)
                selectionState.onDateSelected(date)
            },
            contentAlignment = Alignment.Center,
        ) {
            Text(text = date.dayOfMonth.toString(), fontFamily = Quicksand)
        }
    }
}

fun inDay(items : State<List<TodoTable>>, date : LocalDate): Boolean {
    items.value.forEach {
        if(it.start_date == date.toString() || it.exp_date == date.toString()) {
            return true
        }
    }
    return false
}

@Composable
fun EventRow(item: TodoTable, factory: TodoItemViewModelFactory,
             TodoViewModel: TodoItemViewModel = viewModel(factory = factory), index: Int) {

    var expanded by remember { mutableStateOf(false)}
    var dialogState by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        shape = RoundedCornerShape(2.dp),
        modifier = Modifier
            .wrapContentSize(),
        color = Color(0x77000000)
    ) {
        Column(modifier = Modifier
            .wrapContentSize(),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable { expanded = !expanded },
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(index.toString(), fontFamily = Quicksand, color = Color.White)
                Spacer(modifier = Modifier.size(4.dp))
                Text(item.name, fontFamily = Quicksand, color = Color.White)
            }
            AnimatedVisibility(expanded) {
                Column(modifier = Modifier
                    .wrapContentSize(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Descrição do evento: ${item.description}", fontFamily = Quicksand, color = Color.White)
                    Text(text = (
                            if(item.start_date == item.exp_date){
                                "Data de término: ${item.exp_date}"
                            } else {
                                "Data de início: ${item.start_date}\nData de término: ${item.exp_date}"
                            }
                            ), fontFamily = Quicksand, color = Color.White)
                    Text(text = "Hora do evento: ${item.time}", fontFamily = Quicksand, color = Color.White)
                    Button(onClick = {
                        dialogState = !dialogState
                    }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200))
                    ) {
                        Text(text = "Remover evento",
                             fontSize = 14.sp,
                            textAlign = TextAlign.Center, color = Color.White, fontFamily = Quicksand)
                        if(dialogState) {
                            RemoveDialog(show = dialogState, onDismiss = { dialogState = false },
                                onConfirm = {
                                    TodoViewModel.removeItem(item)
                                    dialogState = false
                                    Toast.makeText(context, "Evento removido", Toast.LENGTH_SHORT).show()
                                            }, "Remover Evento?", "\nEsta ação é irreversível")

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RemoveDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    labelMain: String,
    labelSub: String
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                { Text(text = "Continuar") }
            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = "Cancelar") }
            },
            title = { Text(text = labelMain) },
            text = { Text(text = labelSub) }
        )
    }
}


@Composable
fun datePick(label: String): MutableState<String> {
    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val year: Int
    val month: Int
    val day: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    year = mCalendar.get(Calendar.YEAR)
    month = mCalendar.get(Calendar.MONTH)
    day = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, year, month, day
    )

    Column(modifier = Modifier
        .wrapContentSize()
        .fillMaxWidth(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

        // Creating a button that on
        // click displays/shows the DatePickerDialog
        Button(onClick = {
            mDatePickerDialog.show()
        }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_200)) ) {
            Text(text = (
                    if(mDate.value != ""){
                        "Data selecionada: ${mDate.value}"
                    }else{
                        label
                    }
                    ), fontSize = 14.sp,
                textAlign = TextAlign.Center, color = Color.White, fontFamily = Quicksand)
        }
    }
    return mDate
}

fun getData(context : Context) : List<Subjects> {
    val files: Array<String> = context.fileList()
    val subjects = listOf(
        Subjects("Artes"),
        Subjects("Bio"),
        Subjects("Ed.Fis"),
        Subjects("Fis"),
        Subjects("Quim"),
        Subjects("Mat"),
        Subjects("Socio"),
        Subjects("Filo"),
        Subjects("Hist"),
        Subjects("Geo"),
        Subjects("Port"),
        Subjects("Ing")
    )
    val results = mutableListOf<Subjects>()

    if(files.isEmpty()) {
        context.openFileOutput("grades.txt", Context.MODE_PRIVATE).use {
            for(i in subjects.indices) {
                it.write(serialize(subjects[i]).toByteArray())
                it.write("\n".toByteArray())
            }
        }
    }
    else {
        context.openFileInput("grades.txt").bufferedReader().forEachLine { line ->
            results.add(deserialize(line))
        }
    }
    return results
}

@Composable
fun StatsScreen() {
    val context = LocalContext.current

    var gradeText by remember { mutableStateOf("")}
    var subjectText by remember { mutableStateOf("")}
    var numberText by remember { mutableStateOf("")}

    var expanded by remember { mutableStateOf(false)}
    var showNum by remember { mutableStateOf(false)}

    val icon = if (expanded)
        Icons.Rounded.KeyboardArrowDown
    else
        Icons.Rounded.KeyboardArrowRight

    val nIcon = if (showNum)
        Icons.Rounded.KeyboardArrowDown
    else
        Icons.Rounded.KeyboardArrowRight
    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}
    var numberTextSize by remember { mutableStateOf(Size.Zero)}

    val subjects : List<Subjects> = getData(context)
    val tests = listOf(
        "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.teal_700))
            .wrapContentSize(Alignment.Center)
            .verticalScroll(rememberScrollState())
    ) {
        Card(elevation = 3.dp,
            shape = RoundedCornerShape(0.dp),
            backgroundColor = colorResource(id = R.color.gray_200)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(text = "Notas", style = MaterialTheme.typography.h4, textAlign = TextAlign.Center,
                    color = Color.White, fontFamily = Quicksand)
                Card(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(10.dp),
                    backgroundColor = MaterialTheme.colors.surface
                ) {
                    Demo_Table(subjects)
                }
                Card(
                    elevation = 0.dp,
                    shape = RoundedCornerShape(0.dp),
                    backgroundColor = colorResource(id = R.color.gray_200)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Adicionar nota na tabela", style = MaterialTheme.typography.body1, textAlign = TextAlign.Center,
                            color = Color.White, fontFamily = Quicksand)
                        OutlinedTextField(value = subjectText,
                            onValueChange = { subjectText = it},
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    // This value is used to assign to
                                    // the DropDown the same width
                                    mTextFieldSize = coordinates.size.toSize()
                                },
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrect = true,
                                keyboardType = KeyboardType.Text
                            ),
                            readOnly = true,
                            singleLine = true,
                            label = { Text(text = "Matéria", style = MaterialTheme.typography.caption,
                                color = Color.White, fontFamily = Quicksand) },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontFamily = Quicksand
                            ),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = colorResource(id = R.color.teal_200),
                                unfocusedBorderColor = Color.White,
                                cursorColor = Color.White),
                            trailingIcon = {
                                Icon(icon,"subject selector",
                                    Modifier.clickable { expanded = !expanded }, tint = Color.White)
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .width(with(LocalDensity.current){mTextFieldSize.width.toDp()})
                        ) {
                            subjects.forEach { label ->
                                DropdownMenuItem(onClick = {
                                    subjectText = label.name
                                    expanded = false
                                }
                                ) {
                                    Text(text = label.name, fontFamily = Quicksand)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Row {
                            OutlinedTextField(value = gradeText,
                                onValueChange = { gradeText = it},
                                modifier = Modifier.fillMaxWidth(0.5f),
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = true,
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                label = { Text(text = "Nota obtida", style = MaterialTheme.typography.caption,
                                    color = Color.White, fontFamily = Quicksand) },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = Quicksand
                                ),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = colorResource(id = R.color.teal_200),
                                    unfocusedBorderColor = Color.White,
                                    cursorColor = Color.White))
                            OutlinedTextField(value = numberText,
                                onValueChange = { numberText = it},
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .onGloballyPositioned { coordinates ->
                                        // This value is used to assign to
                                        // the DropDown the same width
                                        numberTextSize = coordinates.size.toSize()
                                    },
                                keyboardOptions = KeyboardOptions(
                                    capitalization = KeyboardCapitalization.None,
                                    autoCorrect = true,
                                    keyboardType = KeyboardType.Number
                                ),
                                singleLine = true,
                                label = { Text(text = "Nª da avaliação", style = MaterialTheme.typography.caption,
                                    color = Color.White, fontFamily = Quicksand) },
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontFamily = Quicksand
                                ),
                                readOnly = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = colorResource(id = R.color.teal_200),
                                    unfocusedBorderColor = Color.White,
                                    cursorColor = Color.White),
                                trailingIcon = {
                                    Icon(nIcon,"ae selector",
                                        Modifier.clickable { showNum = !showNum }, tint = Color.White)
                                })
                            DropdownMenu(
                                expanded = showNum,
                                onDismissRequest = { showNum = false },
                                modifier = Modifier
                                    .width(with(LocalDensity.current){numberTextSize.width.toDp()})
                            ) {
                                tests.forEach { label ->
                                    DropdownMenuItem(onClick = {
                                        numberText = label
                                        showNum = false
                                    }
                                    ) {
                                        Text(text = label, fontFamily = Quicksand)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        AnimatedVisibility((gradeText.isNotEmpty() && subjectText.isNotEmpty() && numberText.isNotEmpty())) {
                            Button(
                                onClick = {
                                    subjects.forEach {
                                        if(subjectText == it.name) {
                                            when(numberText) {
                                                "A1" -> it.a1 = gradeText.toFloat()
                                                "A2" -> it.a2 = gradeText.toFloat()
                                                "A3" -> it.a3 = gradeText.toFloat()
                                                "A4" -> it.a4 = gradeText.toFloat()
                                                "A5" -> it.a5 = gradeText.toFloat()
                                                "A6" -> it.a6 = gradeText.toFloat()
                                                "A7" -> it.a7 = gradeText.toFloat()
                                                "A8" -> it.a8 = gradeText.toFloat()
                                                "A9" -> it.a9 = gradeText.toFloat()
                                                else -> Toast.makeText(context, "$numberText não é uma prova válida", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        it.media = (it.a1 + it.a2 + it.a3 + it.a4 + it.a5 + it.a6 + it.a7 + it.a8 + it.a9)/9
                                    }
                                    context.openFileOutput("grades.txt", Context.MODE_PRIVATE).use {
                                        for(i in subjects.indices) {
                                            it.write(serialize(subjects[i]).toByteArray())
                                            it.write("\n".toByteArray())
                                        }
                                    }
                                    Toast.makeText(context, "Nota da $numberText de $subjectText adicionada", Toast.LENGTH_SHORT).show()
                                    numberText = ""
                                    subjectText = ""
                                    gradeText = ""
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.teal_700))
                            ) {
                                Text(text = "Adicionar Nota", style = MaterialTheme.typography.button, textAlign = TextAlign.Center,
                                    color = Color.White, fontFamily = Quicksand)
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(text = "As notas são atualizadas quando você abre esta aba", style = MaterialTheme.typography.caption,
                            color = Color.White, fontFamily = Quicksand)
                    }
                }
            }
        }
    }
}

@Composable
fun LineChartView(yPoints: MutableList<Float>) {
    Card(elevation = 5.dp,
        shape = RoundedCornerShape(5.dp),
        backgroundColor = MaterialTheme.colors.surface,
        ) {
        LineChart(
            lineChartData = LineChartData(
                points = listOf(
                    Point(yPoints[0],"A1"),
                    Point(yPoints[1],"A2"),
                    Point(yPoints[2],"A3"),
                    Point(yPoints[3],"A4"),
                    Point(yPoints[4],"A5"),
                    Point(yPoints[5],"A6"),
                    Point(yPoints[6],"A7"),
                    Point(yPoints[7],"A8"),
                    Point(yPoints[8],"A9")
                )
            ),
            // Optional properties.
            modifier = Modifier.size(400.dp),
            animation = simpleChartAnimation(),
            pointDrawer = FilledCircularPointDrawer(color = colorResource(id = R.color.teal_700)),
            lineDrawer = SolidLineDrawer(color = colorResource(id = R.color.teal_200)),
            xAxisDrawer = SimpleXAxisDrawer(),
            yAxisDrawer = SimpleYAxisDrawer(drawLabelEvery = 5),
            horizontalOffset = 10f
        )
    }
}

/**
 * The horizontally scrollable table with header and content.
 * @param columnCount the count of columns in the table
 * @param cellWidth the width of column, can be configured based on index of the column.
 * @param data the data to populate table.
 * @param modifier the modifier to apply to this layout node.
 * @param headerCellContent a block which describes the header cell content.
 * @param cellContent a block which describes the cell content.
 */
@Composable
fun Table(
    columnCount: Int,
    cellWidth: (index: Int) -> Dp,
    data: List<Subjects>,
    modifier: Modifier = Modifier,
    headerCellContent: @Composable (index: Int) -> Unit,
    cellContent: @Composable (index: Int, item: Subjects) -> Unit
) {
    Surface(
        modifier = modifier
    ) {
        LazyRow(
            modifier = Modifier.padding(16.dp)
        ) {
            items((0 until columnCount).toList()) { columnIndex ->
                Column {
                    (0..data.size).forEach { index ->
                        Surface(
                            border = BorderStroke(1.dp, Color.LightGray),
                            contentColor = Color.Transparent,
                            modifier = Modifier
                                .width(cellWidth(columnIndex))
                        ) {
                            if (index == 0) headerCellContent(columnIndex)
                            else cellContent(columnIndex, data[index - 1])
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Demo_Table(subjects: List<Subjects>) {
    var dialogState by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    val grades = remember { mutableStateListOf(0f) }

    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 120.dp
            10 -> 100.dp
            else -> 60.dp
        }
    }
    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Matéria"
            1 -> "A1"
            2 -> "A2"
            3 -> "A3"
            4 -> "A4"
            5 -> "A5"
            6 -> "A6"
            7 -> "A7"
            8 -> "A8"
            9 -> "A9"
            10 -> "média"
            else -> ""
        }

        Text(
            text = value,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontFamily = Quicksand,
            fontWeight = FontWeight.Black
        )
    }
    val cellText: @Composable (Int, Subjects) -> Unit = { index, item ->
        val value = when (index) {
            0 -> item.name
            1 -> item.a1.toString()
            2 -> item.a2.toString()
            3 -> item.a3.toString()
            4 -> item.a4.toString()
            5 -> item.a5.toString()
            6 -> item.a6.toString()
            7 -> item.a7.toString()
            8 -> item.a8.toString()
            9 -> item.a9.toString()
            10 -> item.media.toString()
            else -> ""
        }

        if (index != 0) {
            ClickableText(
                text = AnnotatedString(value),
                modifier = Modifier.padding(5.dp),
                style = androidx.compose.ui.text.TextStyle(
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontFamily = Quicksand,
                color = if (value.toFloat() < 6.0f) Color.Red
                    else if (value.toFloat() == 6.0f) Color.Blue
                    else Color.Green
            ), onClick = {
                    grades.clear()
                    dialogState = !dialogState
                    itemName = value
                    grades.addAll(listOf(item.a1, item.a2, item.a3, item.a4, item.a5,
                        item.a6, item.a7, item.a8, item.a9))
                    Log.d("grades", grades.toString() + grades.size)
                         },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        } else {
            ClickableText(
                text = AnnotatedString(value),
                modifier = Modifier.padding(5.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    fontFamily = Quicksand
                ), onClick = {
                    grades.clear()
                    dialogState = !dialogState
                    itemName = value
                    grades.addAll(listOf(item.a1, item.a2, item.a3, item.a4, item.a5,
                        item.a6, item.a7, item.a8, item.a9))
                    Log.d("grades", grades.toString() + grades.size)
                             },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }
    }
    if (dialogState) {
        Graph(show = dialogState, onDismiss = { dialogState = !dialogState },
            onConfirm = { dialogState = !dialogState},
            itemName = itemName,
            grades = grades
        )
    }

    Column {
        Table(
            columnCount = 11,
            cellWidth = cellWidth,
            data = subjects,
            headerCellContent = headerCellTitle,
            cellContent = cellText
        )
        Spacer(modifier = Modifier.height(5.dp))

    }
}


@Composable
fun Graph(show: Boolean,
          onDismiss: () -> Unit,
          onConfirm: () -> Unit,
          itemName: String,
          grades: MutableList<Float>
) {
    if(show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                { Text(text = "Fechar") }
            },
            title = { Text(text = "Gráfico de notas: $itemName") },
            text = { LineChartView(yPoints = grades) }
        )
    }
}