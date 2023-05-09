package ru.hse.control_system_v2.ui

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import androidx.room.RoomDatabase
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.junit.Ignore
import ru.hse.control_system_v2.App
import ru.hse.control_system_v2.App.Companion.context
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.connection.ConnectionManager
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import ru.hse.control_system_v2.data.AppDatabase
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel


class MainViewModel : ViewModel() {

    private val _currentVisibleFragment = MutableLiveData<NavDestination>()

    fun setCurrentVisibleFragment(destination: NavDestination) {
        _currentVisibleFragment.value = destination
    }

    fun getCurrentVisibleFragment(): NavDestination? {
        return _currentVisibleFragment.value
    }

    private val _permissionList = MutableLiveData<List<String>>()
    val permissionList: LiveData<List<String>> = _permissionList

    // Используем функцию-расширение для проверки разрешений
    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    // Используем функцию-расширение для создания диалога
    private fun Context.createPermissionDialog(): AlertDialog {
        return MaterialAlertDialogBuilder(
            this,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog
        ).apply {
            setTitle(getString(R.string.error))
            setMessage(getString(R.string.dialog_permissions_bluetooth_rationale))
            setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                startActivity(intent)
            }
        }.create()
    }

    // Используем константы для разрешений
    @RequiresApi(Build.VERSION_CODES.S)
    private val BLUETOOTH_CONNECT_PERMISSION = Manifest.permission.BLUETOOTH_CONNECT
    private val WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
    private val BLUETOOTH_ADMIN_PERMISSION = Manifest.permission.BLUETOOTH_ADMIN
    private val CHANGE_WIFI_STATE = Manifest.permission.CHANGE_WIFI_STATE
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val MANAGE_WIFI_NETWORK_SELECTION = Manifest.permission.MANAGE_WIFI_NETWORK_SELECTION

    // Используем функцию when для проверки версии SDK
    fun requestPermissions() {
        val list = mutableListOf<String>()
        when {
            // Добавляем условие для разрешения на подключение к Bluetooth
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !context.isPermissionGranted(BLUETOOTH_CONNECT_PERMISSION) -> {
                list.add(BLUETOOTH_CONNECT_PERMISSION)
            }
            !context.isPermissionGranted(BLUETOOTH_ADMIN_PERMISSION) -> {
                list.add(BLUETOOTH_ADMIN_PERMISSION)
            }
            !context.isPermissionGranted(BLUETOOTH_ADMIN_PERMISSION) -> {
                list.add(CHANGE_WIFI_STATE)
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !context.isPermissionGranted(BLUETOOTH_ADMIN_PERMISSION) -> {
                list.add(MANAGE_WIFI_NETWORK_SELECTION)
            }
            // Добавляем условие для разрешения на запись во внешнее хранилище
            !context.isPermissionGranted(WRITE_EXTERNAL_STORAGE_PERMISSION) -> {
                list.add(WRITE_EXTERNAL_STORAGE_PERMISSION)
            }
        }
        _permissionList.value = list
    }

    // Используем функцию filterIndexed для фильтрации отклоненных разрешений
    fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val deniedPermissions = permissions.filterIndexed { index, _ ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }
        _permissionList.value = deniedPermissions
        // Создаем и показываем диалог с помощью функции-расширения
        context.createPermissionDialog().show()
    }


    private val database = AppDatabase.getAppDataBase(App.instance)
    private val deviceDao = database.deviceItemTypeDao()
    private val _devices = deviceDao?.getAll()
    val devices: Flow<List<DeviceModel>>?
        get() = _devices

    lateinit var selectedDevices: ArrayList<DeviceModel>
    init{

    }

    // Метод для освобождения ресурсов при завершении работы модели представления
    override fun onCleared() {
        super.onCleared()

    }
}