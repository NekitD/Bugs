package com.example.myapplication

// Коммит 1 - создание класса структуры данных игрока
data class PlayerData(
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficulty: Int = 1,
    val birthDate: String = "",
    val zodiacSign: String = "",
    val zodiacSym: String = ""
)