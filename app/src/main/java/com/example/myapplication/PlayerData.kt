package com.example.myapplication

data class PlayerData(
    val fullName: String = "",
    val gender: String = "",
    val course: String = "",
    val difficulty: Int = 1,
    val birthDate: String = "",
    val zodiacSign: String = "",
    val zodiacSym: String = ""
)

fun getZodiacSign(day: Int, month: Int): Pair<String, String> {
    return when {
        (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "Овен" to "\u2648"
        (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "Телец" to "\u2649"
        (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "Близнецы" to "\u264A"
        (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "Рак" to "\u264B"
        (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "Лев" to "\u264C"
        (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "Дева" to "\u264D"
        (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "Весы" to "\u264E"
        (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "Скорпион" to "\u264F"
        (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "Стрелец" to "\u2650"
        (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "Козерог" to "\u2651"
        (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "Водолей" to "\u2652"
        else -> "Рыбы" to "\u2653"
    }
}
