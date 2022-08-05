package com.example.organizatudo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.organizatudo.database.projects.ProjectsDatabase
import com.example.organizatudo.database.projects.ProjectsRepositoryImpl
import com.example.organizatudo.database.todoList.TodoItemsDatabase
import com.example.organizatudo.database.todoList.TodoItemsRepositoryImpl
import com.example.organizatudo.homeContent.*
import com.example.organizatudo.navigation.NavigationItem
import com.example.organizatudo.navigation.NavigationItemDB
import com.example.organizatudo.navigation.NavigationItemHome
import com.example.organizatudo.ui.factory.ProjectViewModelFactory
import com.example.organizatudo.ui.factory.TodoItemViewModelFactory
import com.example.organizatudo.ui.theme.Quicksand
import com.example.organizatudo.ui.viewmodel.MainViewModel
import com.example.organizatudo.ui.viewmodel.StopWatchViewModel
import com.example.organizatudo.ui.viewmodel.ChronometerViewModel
import com.example.organizatudo.ui.viewmodel.InTimeViewModel
import java.util.*

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val todoItemDao = TodoItemsDatabase.getInstance(application).itemDAO
        val todoItemsRepository = TodoItemsRepositoryImpl(todoItemDao)
        val todoItemViewModelFactory = TodoItemViewModelFactory(todoItemsRepository)

        val projectDao = ProjectsDatabase.getInstance(application).projectDao
        val projectItemsRepository = ProjectsRepositoryImpl(projectDao)
        val projectFactory = ProjectViewModelFactory(projectItemsRepository)

        val timerviewModel by viewModels<ChronometerViewModel>()
        val watchViewModel by viewModels<StopWatchViewModel>()
        val inTimeViewModel by viewModels<InTimeViewModel>()
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                viewModel.isLoading.value
            }
        }
        setContent {

            MainScreen(
                todoItemViewModelFactory = todoItemViewModelFactory,
                projectViewModelFactory = projectFactory,
                inTimeViewModel,
                timerviewModel, watchViewModel
            )
        }
    }
}
@Composable
fun MainScreen(
    todoItemViewModelFactory: TodoItemViewModelFactory,
    projectViewModelFactory: ProjectViewModelFactory,
    inTimeViewModel: InTimeViewModel,
    chronometerViewModel: ChronometerViewModel, watchViewModel: StopWatchViewModel
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopBar(navController) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Navigation(
                navController,
                todoItemViewModelFactory,
                projectViewModelFactory,
                inTimeViewModel,
                chronometerViewModel,
                watchViewModel)
        }
    }
}


@Composable
fun Navigation(
    navController: NavHostController,
    todoItemViewModelFactory: TodoItemViewModelFactory,
    projectViewModelFactory: ProjectViewModelFactory,
    inTimeViewModel: InTimeViewModel,
    chronometerViewModel: ChronometerViewModel, watchViewModel: StopWatchViewModel
) {
    NavHost(navController, startDestination = NavigationItem.Home.route) {
        composable(NavigationItem.Home.route) {
            HomeScreen(navController, todoItemViewModelFactory, projectViewModelFactory)
            inTimeViewModel.stop()
        }
        composable(NavigationItem.Calendar.route) {
            CalendarScreen(todoItemViewModelFactory)
        }
        composable(NavigationItem.Stats.route) {
            StatsScreen()

        }
        //Home Routes
        composable(NavigationItemHome.Pomodoro.route) {
            PomodoroScreen(navController, NavigationItemHome.Pomodoro)
        }
        composable(NavigationItemHome.Pomodoro.routeActivity + "/{workTime}" + "/{freeTime}" + "/{longFreeTime}",
        arguments = listOf(
            navArgument("workTime") {type = NavType.LongType},
            navArgument("freeTime") {type = NavType.LongType},
            navArgument("longFreeTime") {type = NavType.LongType})
        ) { backStackEntry ->
            PomodoroActivityScreen(
                backStackEntry.arguments?.getLong("workTime")!!,
                backStackEntry.arguments?.getLong("freeTime")!!,
                backStackEntry.arguments?.getLong("longFreeTime")!!,
                watchViewModel)
        }
        composable(NavigationItemHome.FlowTime.route) {
            FlowTimeScreen(navController, NavigationItemHome.FlowTime)
        }
        composable(NavigationItemHome.FlowTime.routeActivity) {
            FlowTimeActivityScreen(chronometerViewModel)
        }
        composable(NavigationItemHome.JustInTime.route) {
            JustInTimeScreen(navController, router = NavigationItemHome.JustInTime)
        }
        composable(NavigationItemHome.JustInTime.routeActivity + "/{workTime}",
        arguments = listOf(
            navArgument("workTime") {type = NavType.LongType})
        ) { backStackEntry ->
            JustInTimeActivityScreen(inTimeViewModel,
            backStackEntry.arguments?.getLong("workTime")!!)
        }
        composable(NavigationItemDB.AddProject.route) {
            AddProject(navController, projectViewModelFactory)

        }
        composable(NavigationItemDB.ProjectScreen.route + "/{projectId}",
        arguments = listOf(
            navArgument("projectId") {type = NavType.IntType})
            ) { backStackEntry ->
            ProjectScreen(projectViewModelFactory,
            backStackEntry.arguments?.getInt("projectId")!!)
        }
    }
}

@Composable
fun TopBar(navController : NavController) {

    var actionBarTitle by rememberSaveable { mutableStateOf("Home") }
    var isHome by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(navController, isHome) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            // You can map the title based on the route using:
            actionBarTitle = backStackEntry.destination.route.toString()
                .replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            isHome = actionBarTitle == NavigationItem.Home.route.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    }

    TopAppBar(
        title = { Text(
            text = if("/" in actionBarTitle){
                actionBarTitle.replaceAfter('/', "").dropLast(1)
           }
            else actionBarTitle,
            fontSize = 18.sp,
            fontFamily = Quicksand)},
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() },
                enabled = !isHome
            ) {
                Crossfade(targetState = isHome) { isChecked ->
                    // note that it's required to use the value passed by Crossfade
                    // instead of your state value
                    if (isChecked) {
                        Icon(painterResource(id = R.drawable.ic_home_anim), contentDescription = "Home")
                    } else {
                        Icon(painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Arrow Back")
                    }
                }
            }
        },
        backgroundColor = colorResource(id = R.color.teal_700),
        contentColor = Color.White,
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Calendar,
        NavigationItem.Stats
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.black),
        contentColor = Color.White
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = {
                        if(currentRoute == item.route) {
                            Icon(
                                painterResource(id = item.iconEnabled),
                                contentDescription = item.title)
                        }
                        else {
                            Icon(
                                painterResource(id = item.iconDisabled),
                                contentDescription = item.title)
                        }},
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize()
                    .align(Alignment.CenterVertically)
                    .widthIn(25.dp),
                label = {
                    Text(
                        text = item.title,
                        softWrap = false) },
                selectedContentColor = Color.White,
                unselectedContentColor = Color.White.copy(0.4f),
                alwaysShowLabel = false,
                selected = currentRoute == item.route,
                onClick = {

                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    // BottomNavigationBar()
}