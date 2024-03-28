package ru.hse.control_system_v2.ui.fragments.connection_settings

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Capability
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.api.Device
import ru.hse.control_system_v2.model.entities.universal.classes.device_desc.room.DeviceRepository

class ConnectionTypeViewModel () : ViewModel() {
    private val deviceRepository: DeviceRepository = App.deviceRepository
    private val _devices = MutableLiveData<List<Device>>()
    val devices: LiveData<List<Device>> = _devices

    init {
        loadDevices()
    }

    private fun loadDevices() {
        viewModelScope.launch {
            val devices = deviceRepository.getDevices()
            _devices.postValue(devices)
        }
    }

    fun handleDeviceAction(deviceId: String, capability: Capability) {
        viewModelScope.launch {
            try {
                val result = deviceRepository.handleDeviceAction(deviceId, capability)
                Log.i("ConnectionTypeViewModel", "Result: $result")
            } catch (e: Exception) {
                // Обработка ошибки
            }
        }
    }
}