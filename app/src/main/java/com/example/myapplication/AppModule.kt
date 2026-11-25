package com.example.myapplication

import com.example.myapplication.game.GameViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::GameViewModel)
}