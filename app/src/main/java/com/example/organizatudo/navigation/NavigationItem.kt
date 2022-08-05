package com.example.organizatudo.navigation

import com.example.organizatudo.R
import com.example.organizatudo.ui.items.RoutineItem
import com.example.organizatudo.homeContent.RoutineItems

sealed class NavigationItem(var route: String, var iconDisabled: Int, var iconEnabled: Int, var title: String) {
    // Bottom navigation items
    object Home : NavigationItem("início", R.drawable.ic_home, R.drawable.ic_home_enabled, "Início")
    object Calendar : NavigationItem("calendário", R.drawable.ic_calendar, R.drawable.ic_calendar_enabled, "Calendário")
    object Stats : NavigationItem("relatório", R.drawable.ic_stats, R.drawable.ic_stats_active, "Relatório")
}

sealed class NavigationItemHome(var route: String, var routeActivity: String, var routineItem: RoutineItem) {

    object Pomodoro : NavigationItemHome("Pomodoro","Pomodoro GO", RoutineItems[0])
    object FlowTime : NavigationItemHome("FlowTime","FlowTime GO", RoutineItems[1])
    object JustInTime : NavigationItemHome("JustInTime","JustInTime GO", RoutineItems[2])
}

sealed class NavigationItemDB(var route: String) {

    object AddProject : NavigationItemDB("adicionar projeto")
    object ProjectScreen : NavigationItemDB("projeto")
}

