package com.example.appmobile.data

data class Avatar(
    val id: String,
    val name: String,
    val emoji: String,
    val color: String
)

object AvatarRepository {
    val availableAvatars = listOf(
        Avatar("car_red", "Coche Rojo", "🏎️", "#FF0000"),
        Avatar("car_blue", "Coche Azul", "🏎️", "#0000FF"),
        Avatar("car_green", "Coche Verde", "🏎️", "#00FF00"),
        Avatar("car_yellow", "Coche Amarillo", "🏎️", "#FFFF00"),
        Avatar("car_purple", "Coche Púrpura", "🏎️", "#800080"),
        Avatar("car_orange", "Coche Naranja", "🏎️", "#FFA500"),
        Avatar("car_pink", "Coche Rosa", "🏎️", "#FFC0CB"),
        Avatar("car_cyan", "Coche Cian", "🏎️", "#00FFFF"),
        Avatar("motorcycle_red", "Moto Roja", "🏍️", "#FF0000"),
        Avatar("motorcycle_blue", "Moto Azul", "🏍️", "#0000FF"),
        Avatar("motorcycle_green", "Moto Verde", "🏍️", "#00FF00"),
        Avatar("motorcycle_yellow", "Moto Amarilla", "🏍️", "#FFFF00"),
        Avatar("bike_red", "Bici Roja", "🚲", "#FF0000"),
        Avatar("bike_blue", "Bici Azul", "🚲", "#0000FF"),
        Avatar("bike_green", "Bici Verde", "🚲", "#00FF00"),
        Avatar("bike_yellow", "Bici Amarilla", "🚲", "#FFFF00")
    )

    fun getAvatarById(id: String): Avatar? {
        return availableAvatars.find { it.id == id }
    }
} 