package ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api

data class Property(
    val type: String,
    val retrievable: Boolean,
    val parameters: Map<String, Any>,
    val state: Map<String, Any>?,
    val lastUpdated: Float
)