package ru.hse.control_system_v2.ui.fragments.connection_settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.model.db.AppDatabase
import ru.hse.control_system_v2.model.entities.Device

class ConnectionTypeViewModel : ViewModel() {

    private val database = AppDatabase.getAppDataBase(App.instance)
    private val deviceDao = database.deviceItemTypeDao()
    private val _devices = deviceDao?.getAll()
    val devices: Flow<List<Device>>?
        get() = _devices

    lateinit var selectedDevices: ArrayList<Device>

}