package ru.hse.control_system_v2.ui

import android.Manifest.permission.BLUETOOTH_ADMIN
import android.Manifest.permission.BLUETOOTH_ADVERTISE
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.Manifest.permission.BLUETOOTH_SCAN
import android.Manifest.permission.CHANGE_WIFI_STATE
import android.Manifest.permission.MANAGE_WIFI_INTERFACES
import android.Manifest.permission.MANAGE_WIFI_NETWORK_SELECTION
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.utility.AppConstants.APP_LOG_TAG
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.domain.connection.ConnectionFactory
import ru.hse.control_system_v2.databinding.ActivityMainBinding
import ru.hse.control_system_v2.ui.fragments.home.MainMenuFragment
import ru.hse.control_system_v2.ui.fragments.home.MainViewModel
import ru.hse.control_system_v2.utility.AppConstants.BLUETOOTH_ADMIN_PERMISSION
import ru.hse.control_system_v2.utility.AppConstants.BLUETOOTH_CONNECT_PERMISSION
import ru.hse.control_system_v2.utility.AppConstants.WRITE_EXTERNAL_STORAGE_PERMISSION
import ru.hse.control_system_v2.utility.ThemeUtils.onActivityCreateSetTheme
import java.util.*

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        onActivityCreateSetTheme(this)
        setContentView(binding.root)
        setUpNavigation()
        checkForBtAdapter()
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.main_toolbar_menu)
        toolbar.setOnMenuItemClickListener {
            when (viewModel.getCurrentVisibleFragment()?.id) {
                R.id.mainMenuFragment -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app)
                )
                R.id.settingsFragment -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_settings)
                )
                R.id.deviceMenuFragment -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_device_menu)
                )
                R.id.addDeviceFragment -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_add_device)
                )
                else -> {}
            }
            true
        }
    }

    private fun setUpNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        navHostFragment?.let { navHost ->
            setupWithNavController(
                binding.bottomnav,
                navHost.navController
            )

            navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.e(APP_LOG_TAG, "onDestinationChanged: " + destination.label)
                viewModel.setCurrentVisibleFragment(destination)
            }

            binding.bottomnav.setOnItemSelectedListener { item: MenuItem ->
                return@setOnItemSelectedListener when (item.itemId) {
                    R.id.mainMenuFragment -> {
                        navHost.navController.navigate(R.id.mainMenuFragment)
                        true
                    }
                    R.id.settingsFragment -> {
                        navHost.navController.navigate(R.id.settingsFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun checkForBtAdapter() {
        if (!ConnectionFactory.isBtWiFiSupported) {
            val dialog = MaterialAlertDialogBuilder(this,
                    com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog).create()
            dialog.setTitle(getString(R.string.error));
            dialog.setMessage(getString(R.string.suggestionNoBtWiFiAdapter))
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)) {
                dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
            }
            dialog.show()
        }
    }

    public override fun onResume() {
        super.onResume()
        requestMultiplePermissions()
    }

    private var permissionsRequested = false

    private fun requestMultiplePermissions() {
        lifecycleScope.launch {
            permissionList.observe(this@MainActivity) { list ->
                if (list.isNotEmpty() && !permissionsRequested) {
                    permissionsRequested = true
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        list.toTypedArray(),
                        PERMISSION_REQUEST_CODE
                    )
                }
            }
        }
        requestPermissions()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Arrays.stream(grantResults)
                .anyMatch { n: Int -> n != PackageManager.PERMISSION_GRANTED }
        ) {
            if (requestCode == PERMISSION_REQUEST_CODE) {
                onRequestPermissionsResult(permissions, grantResults)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun requestPermissions() {
        val list = mutableListOf<String>()
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (!application.applicationContext.isPermissionGranted(BLUETOOTH_CONNECT)) {
                    list.add(BLUETOOTH_CONNECT)
                    Log.d(APP_LOG_TAG, "Permission missing: $BLUETOOTH_CONNECT")
                }
                if (!application.applicationContext.isPermissionGranted(BLUETOOTH_SCAN)) {
                    list.add(BLUETOOTH_SCAN)
                    Log.d(APP_LOG_TAG, "Permission missing: $BLUETOOTH_SCAN")
                }
            }
            else -> {
                if (!application.applicationContext.isPermissionGranted(BLUETOOTH_ADMIN)) {
                    list.add(BLUETOOTH_ADMIN)
                    Log.d(APP_LOG_TAG, "Permission missing: $BLUETOOTH_ADMIN")
                }
                if (!application.applicationContext.isPermissionGranted(CHANGE_WIFI_STATE)) {
                    list.add(CHANGE_WIFI_STATE)
                    Log.d(APP_LOG_TAG, "Permission missing: $CHANGE_WIFI_STATE")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && !application.applicationContext.isPermissionGranted(MANAGE_WIFI_INTERFACES)) {
                    list.add(MANAGE_WIFI_INTERFACES)
                    Log.d(APP_LOG_TAG, "Permission missing: $MANAGE_WIFI_INTERFACES")
                }
            }
        }
        if (!application.applicationContext.isPermissionGranted(WRITE_EXTERNAL_STORAGE)) {
            list.add(WRITE_EXTERNAL_STORAGE)
            Log.d(APP_LOG_TAG, "Permission missing: $WRITE_EXTERNAL_STORAGE")
        }
        _permissionList.value = list
    }

    private val _permissionList = MutableLiveData<List<String>>()
    private val permissionList: LiveData<List<String>> = _permissionList

    private fun Context.isPermissionGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun onRequestPermissionsResult(
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        val deniedPermissions = permissions.filterIndexed { index, _ ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }
        _permissionList.value = deniedPermissions
        createPermissionDialog().show()
    }

    private fun createPermissionDialog(): AlertDialog {
        val context = this
        val deniedPermissions = _permissionList.value ?: emptyList()
        val message = when {
            deniedPermissions.contains(BLUETOOTH_CONNECT_PERMISSION) -> {
                getString(R.string.dialog_permissions_bluetooth_connect_rationale)
            }
            deniedPermissions.contains(BLUETOOTH_ADMIN_PERMISSION) -> {
                getString(R.string.dialog_permissions_bluetooth_admin_rationale)
            }
            deniedPermissions.contains(CHANGE_WIFI_STATE) -> {
                getString(R.string.dialog_permissions_change_wifi_state_rationale)
            }
            deniedPermissions.contains(MANAGE_WIFI_NETWORK_SELECTION) -> {
                getString(R.string.dialog_permissions_manage_wifi_network_selection_rationale)
            }
            deniedPermissions.contains(WRITE_EXTERNAL_STORAGE_PERMISSION) -> {
                getString(R.string.dialog_permissions_write_external_storage_rationale)
            }
            else -> {
                getString(R.string.dialog_permissions_general_rationale)
            }
        }
        return MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_Dialog_Alert)
            .setTitle(R.string.error)
            .setMessage(message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                openAppSettings()
            }
            .create()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        startActivity(intent)
    }

    private val PERMISSION_REQUEST_CODE = 123

    // создает диалоговое окно с 1й кнопкой
    private fun createOneButtonAlertDialog(title: String, content: String) {
        val message = Bundle()
        message.putString("dialogText", content)
        message.putString("dialogTitle", title)
        (supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?)?.
        navController?.navigate(R.id.oneButtonAlertDialogFragment, message)
    }

    val bottomAppBarSize: Int
        get() {
            val resourceId = resources.getIdentifier(
                "design_bottom_navigation_height",
                "dimen",
                this.packageName
            )
            var height = 0
            if (resourceId > 0) {
                height = resources.getDimensionPixelSize(resourceId)
            }
            return height
        }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.container)
        if (currentFragment is MainMenuFragment) {
            handleBackPressedOnMainMenu()
        } else {
            super.onBackPressed()
        }
    }

    private fun handleBackPressedOnMainMenu() {
        if (isDoubleBackPressed()) {
            finish()
        } else {
            showDoubleBackPressSnackBar()
            updateLastBackPressedTime()
        }
    }

    private fun isDoubleBackPressed(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastBackPress = currentTime - lastBackPressedTime
        return timeSinceLastBackPress <= DOUBLE_BACK_PRESS_INTERVAL
    }

    private fun showDoubleBackPressSnackBar() {
        Snackbar.make(
            binding.bottomnav,
            R.string.double_back_click,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun updateLastBackPressedTime() {
        lastBackPressedTime = System.currentTimeMillis()
    }

    companion object {
        private const val DOUBLE_BACK_PRESS_INTERVAL = 2000L
        private var lastBackPressedTime = 0L
    }
}