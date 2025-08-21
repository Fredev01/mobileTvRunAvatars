package com.angelyjesus.carreraavatar.data

data class Avatar(
    val id: String,
    val name: String,
    val emoji: String,
    val color: String,
    val imageUrl: String = "" // URL de la imagen del Pokémon
)

object AvatarRepository {

    // Lista de 6 Pokémon populares y reconocibles para las carreras
    private val pokemonData = listOf(
        Triple(25, "Pikachu", "⚡"), // Pikachu - Eléctrico
        Triple(6, "Charizard", "🔥"), // Charizard - Fuego/Volador
        Triple(9, "Blastoise", "💧"), // Blastoise - Agua
        Triple(3, "Venusaur", "🌿"), // Venusaur - Planta/Veneno
        Triple(94, "Gengar", "👻"), // Gengar - Fantasma/Veneno
        Triple(130, "Gyarados", "🐉") // Gyarados - Agua/Volador
    )

    private val colors = listOf(
        "#FFD700", // Dorado para Pikachu
        "#FF4500", // Rojo anaranjado para Charizard
        "#1E90FF", // Azul para Blastoise
        "#32CD32", // Verde para Venusaur
        "#8A2BE2", // Púrpura para Gengar
        "#4169E1"  // Azul real para Gyarados
    )

    // Generar avatares usando la PokéAPI
    val availableAvatars: List<Avatar> by lazy {
        generatePokemonAvatars()
    }

    private fun generatePokemonAvatars(): List<Avatar> {
        return pokemonData.mapIndexed { index, (pokemonId, pokemonName, emoji) ->
            // URL oficial de la PokéAPI para imágenes de alta calidad
            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$pokemonId.png"

            Avatar(
                id = "pokemon_$pokemonId",
                name = pokemonName,
                emoji = emoji,
                color = colors[index],
                imageUrl = imageUrl
            )
        }
    }

    // Mantener compatibilidad con el código existente
    fun getAvatarById(id: String): Avatar? {
        return availableAvatars.find { it.id == id }
    }

    // Función para obtener un avatar aleatorio
    fun getRandomAvatar(): Avatar {
        return availableAvatars.random()
    }

    // Función para obtener avatar por nombre de Pokémon
    fun getAvatarByPokemonName(name: String): Avatar? {
        return availableAvatars.find { it.name.equals(name, ignoreCase = true) }
    }
}