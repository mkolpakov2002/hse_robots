package ru.hse.control_system_v2.ui.fragments.device_settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.model.db.AppDatabase
import ru.hse.control_system_v2.model.entities.DeviceOld

class DeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentDeviceOld = MutableLiveData<DeviceOld>()
    val currentDeviceOld: LiveData<DeviceOld> = _currentDeviceOld
    private val database = AppDatabase.getInstance(application.applicationContext)
    private val deviceDao = database.deviceOldItemTypeDao()

    fun setCurrentDevice(deviceOld: DeviceOld) {
        _currentDeviceOld.value = deviceOld
    }

    fun updateCurrentDevice(deviceOld: DeviceOld) {
        _currentDeviceOld.value = deviceOld
    }

    fun saveDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            deviceDao?.insertAll(currentDeviceOld.value!!)
        }
    }

    fun deleteDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            deviceDao?.delete(currentDeviceOld.value!!.id)
        }
    }
}