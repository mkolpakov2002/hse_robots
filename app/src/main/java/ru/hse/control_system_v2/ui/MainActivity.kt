package ru.hse.control_system_v2.ui

import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import ru.hse.control_system_v2.utility.AppConstants.APP_LOG_TAG
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.domain.connection.ConnectionFactory
import ru.hse.control_system_v2.databinding.ActivityMainBinding
import ru.hse.control_system_v2.ui.fragments.dialog.OneButtonAlertDialogFragment
import ru.hse.control_system_v2.ui.fragments.home.MainViewModel
import ru.hse.control_system_v2.ui.fragments.home.MainViewModelFactory
import ru.hse.control_system_v2.utility.ThemeUtils.onActivityCreateSetTheme
import java.util.*

class MainActivity : AppCompatActivity(), OneButtonAlertDialogFragment.OnDismissListener{
    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        onActivityCreateSetTheme(this)
        setContentView(binding.root)
        // Создание экземпляра ViewModel с помощью ViewModelProvider
        viewModel = ViewModelProvider(this, MainViewModelFactory())[MainViewModel::class.java]
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
        requestMultiplePermissions()
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

            binding.bottomnav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.mainMenuFragment -> {
                        navHost.navController.navigate(R.id.mainMenuFragment)
                        return@setOnItemSelectedListener true
                    }
                    R.id.settingsFragment -> {
                        navHost.navController.navigate(R.id.settingsFragment)
                        return@setOnItemSelectedListener true
                    }
                }
                false
            }
        }
    }

    // проверка на наличие адаптеров
    private fun checkForBtAdapter() {
        if (!ConnectionFactory.isBtWiFiSupported) {
            // объект Builder для создания диалогового окна
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
    }

    private fun requestMultiplePermissions() {
        viewModel.requestPermissions()
        viewModel.permissionList.observe(this) { list ->
            if (list.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    list.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        }
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
                viewModel.onRequestPermissionsResult(permissions, grantResults)
            }
        }
    }

    val PERMISSION_REQUEST_CODE = 123

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
        if (viewModel.getCurrentVisibleFragment()?.id == R.id.mainMenuFragment) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                //показ сообщения, о необходимости второго нажатия кнопки назад при выходе
                val snackbar = Snackbar
                    .make(
                        binding.bottomnav, getString(R.string.double_back_click),
                        Snackbar.LENGTH_LONG
                    )
                snackbar.show()
            }
            back_pressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDialogDismissed() {
//        if (isBtConnection != null) {
//            enableNetwork()
//        }
    }

    companion object {
        //Переменная для хранения времени между нажатиями кнопки назад
        private var back_pressed: Long = 0
    }
}