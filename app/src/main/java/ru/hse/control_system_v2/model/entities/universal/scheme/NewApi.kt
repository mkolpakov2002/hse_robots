package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
abstract class RequestPayload : APIModel

@Serializable
abstract class Request : APIModel {
    abstract val requestId: String?
}
