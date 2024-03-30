package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
abstract class APIModel {
    fun asJson(): String {
        return Json.encodeToString(this)
    }

    fun asMap(): Map<String, Any?> {
        return Json.decodeFromString(asJson())
    }
}

@Serializable
abstract class GenericAPIModel<T> : APIModel()