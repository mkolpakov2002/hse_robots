package ru.hse.control_system_v2.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.hse.control_system_v2.App.Companion.context
import ru.hse.control_system_v2.R

class MainViewModel : ViewModel() {

    private val _currentVisibleFragment = MutableLiveData<NavDestination?>()

    fun setCurrentVisibleFragment(destination: NavDestination) {
        _currentVisibleFragment.value = destination
    }

    private val _permissionList = MutableLiveData<List<String>>()
    val permissionList: LiveData<List<String>> = _permissionList

    fun requestPermissions() {
        val list = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_DENIED
            ) {
                list.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        _permissionList.value = list
    }

    fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val deniedPermissions = ArrayList<String>()
        for (i in grantResults.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        _permissionList.value = deniedPermissions

        // объект Builder для создания диалогового окна
        //AlertDialog_AppCompat
        val dialog = MaterialAlertDialogBuilder(
            context,
            com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog
        ).create()
        dialog.setTitle(context.getString(R.string.error))
        dialog.setMessage(context.getString(R.string.dialog_permissions_bluetooth_rationale))
        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
        ) { dialog1, _ ->
            dialog1.dismiss()
            val intent = Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            (context as Activity).finish()
        }
        dialog.setCancelable(false)
        dialog.show()

    }
}