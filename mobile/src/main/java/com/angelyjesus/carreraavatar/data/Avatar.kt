package com.angelyjesus.carreraavatar.data

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
        Avatar("motorcycle_red", "Moto Roja", "🏍️", "#FF0000"),
        Avatar("motorcycle_blue", "Moto Azul", "🏍️", "#0000FF"),
        Avatar("bike_red", "Bici Roja", "🚲", "#FF0000")
    )

    fun getAvatarById(id: String): Avatar? {
        return availableAvatars.find { it.id == id }
    }
} 