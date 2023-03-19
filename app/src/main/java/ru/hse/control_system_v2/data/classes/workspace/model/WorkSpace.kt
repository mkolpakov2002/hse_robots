package ru.hse.control_system_v2.data.classes.workspace.model

import kotlinx.serialization.json.Json

/**
 * Определяет рабочее пространство пользователя
 */
@kotlinx.serialization.Serializable
data class WorkSpace(
    val isJoystickEnabled: Boolean,
    val isVideoStreamEnabled: Boolean,
    val isPackageDataEnabled: Boolean
) {
    override fun toString(): String {
        return Json.encodeToString(serializer(), this)
   }
}