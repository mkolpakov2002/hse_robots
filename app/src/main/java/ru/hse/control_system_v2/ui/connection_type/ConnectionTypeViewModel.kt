package ru.hse.control_system_v2.ui.connection_type

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import com.google.android.material.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.data.AppDatabase
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel

class ConnectionTypeViewModel : ViewModel() {

    private val database = AppDatabase.getAppDataBase(App.instance)
    private val deviceDao = database.deviceItemTypeDao()
    private val _devices = deviceDao?.getAll()
    val devices: Flow<List<DeviceModel>>?
        get() = _devices

    lateinit var selectedDevices: ArrayList<DeviceModel>

}