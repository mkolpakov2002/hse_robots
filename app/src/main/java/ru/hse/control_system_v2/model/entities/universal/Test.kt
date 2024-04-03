package ru.hse.control_system_v2.model.entities.universal

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class Project {
    abstract val name: String
}

@Serializable
class OwnedProject(override val name: String, val owner: String) : Project()

fun main() {
    val data: Project = OwnedProject("kotlinx.coroutines", "kotlin")
    val stringData: String = Json.encodeToString(data)
    val response: Project = Json.decodeFromString(stringData)
    println("response == ")
    println(response)
}