package ru.hse.control_system_v2.data.classes.workspace

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.serialization.json.Json
import ru.hse.control_system_v2.data.classes.workspace.model.WorkSpace
import kotlin.properties.Delegates.notNull

object WorkSpaceSharedPreferences {

    private const val KEY = "WorkSpaceSharedPreferences"

    private var sharedPreferences: SharedPreferences by notNull()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(KEY, Context.MODE_PRIVATE)
    }

    fun get(deviceId: Int): WorkSpace? {
        val workSpaceString = sharedPreferences.getString(deviceId.toString(), null) ?: return null
        return Json.decodeFromString(WorkSpace.serializer(), workSpaceString)
    }

    fun set(deviceId: Int, workSpace: WorkSpace) {
        sharedPreferences.edit {
            putString(deviceId.toString(), workSpace.toString())
        }
    }
}