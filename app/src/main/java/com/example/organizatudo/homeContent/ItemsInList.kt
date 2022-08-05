package com.example.organizatudo.homeContent

import com.example.organizatudo.R
import com.example.organizatudo.ui.items.RoutineItem

val RoutineItems: List<RoutineItem> = listOf(
    RoutineItem(name = "O Método Pomodoro",
        img = R.drawable.ic_api_card,
        subtitle = "Organize seu tempo de estudo entre trabalho e descanso com a ajuda de um temporizador.",
        contentDesc = "The Pomodoro Method Logo"),
    RoutineItem(name = "O Método FlowTime",
        img = R.drawable.ic_api_card,
        subtitle = "Inicie o cronômetro e começe a trabalhar. Quando terminar ou se sentir cansado, veremos quanto tempo você trabalhou e calcularemos seu tempo de descanso",
        contentDesc = "The Pomodoro Method Logo"),
    RoutineItem(name = "O Método Just In Time",
        img = R.drawable.ic_api_card,
        subtitle = "Defina uma meta, um temporizador e começe a trabalhar. Tente terminar antes do tempo acabar.",
        contentDesc = "The Pomodoro Method Logo")
)
