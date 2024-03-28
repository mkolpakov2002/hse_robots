package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class Capability(
    val type: String,
    val retrievable: Boolean? = null,
    val parameters: Map<String, Any>? = null,
    val state: Map<String, Any>?  = null,
    val lastUpdated: Float?  = null
)