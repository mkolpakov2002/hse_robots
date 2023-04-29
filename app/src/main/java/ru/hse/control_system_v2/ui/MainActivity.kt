package ru.hse.control_system_v2.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import ru.hse.control_system_v2.AppConstants.APP_LOG_TAG
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import ru.hse.control_system_v2.databinding.ActivityMainBinding
import ru.hse.control_system_v2.ui.dialog.OneButtonAlertDialogFragment
import ru.hse.control_system_v2.ui.home.MainMenuFragment
import ru.hse.control_system_v2.ui.theming.ThemeUtils.onActivityCreateSetTheme
import java.util.*

class MainActivity : AppCompatActivity(), OneButtonAlertDialogFragment.OnDismissListener,
    PassDataToActivityInterface {
    private val binding: ActivityMainBinding? = null
    private var main_bottom_menu: BottomNavigationView? = null
    var currentVisibleFragment: NavDestination? = null
        private set
    private var navHostFragment: NavHostFragment? = null
    private var navController: NavController? = null
    private var isBtConnection: Boolean? = null

    private lateinit var viewModel: MainViewModel
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view: View = binding!!.root
        setContentView(view)
        onActivityCreateSetTheme(this)
        setContentView(R.layout.activity_main)
        // Создание экземпляра ViewModel с помощью ViewModelProvider
        viewModel = ViewModelProvider(this, MainViewModelFactory()).get(MainViewModel::class.java)
        setUpNavigation()


        checkForBtAdapter()
        //        if (App.isServiceConnecting()) {
//            navController.navigate(R.id.connection_dialog);
//        }
        val toolbar = binding.toolbar
        toolbar.inflateMenu(R.menu.main_toolbar_menu)
        toolbar.setOnMenuItemClickListener {
            val currentFragment = Objects.requireNonNull<CharSequence?>(
                currentVisibleFragment?.label
            ).toString()
            when (currentFragment) {
                "main" -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app)
                )
                "settings" -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_settings)
                )
                "DeviceMenuFragment" -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_device_menu)
                )
                "AddDeviceFragment" -> createOneButtonAlertDialog(
                    getString(R.string.instruction_alert),
                    getString(R.string.instruction_for_app_add_device)
                )
                else -> {}
            }
            true
        }
        registerReceiver(mMessageReceiverNeedToStartBtService, IntentFilter("startingBtService"))
        registerReceiver(
            mMessageReceiverNeedToStartWiFiService,
            IntentFilter("startingWiFiService")
        )
        registerReceiver(mMessageReceiverNotSuccess, IntentFilter("not_success"))
        registerReceiver(mMessageReceiverSuccess, IntentFilter("success"))
        isBtConnection = null

//        if (buttonToConnectViaWiFi != null) {
//            buttonToConnectViaWiFi.setOnClickListener(view1 -> {
//                if (App.isWiFiSupported()) {
//                    boolean isConnectionPossible = true;
//                    for (DeviceModel current : App.getDevicesList()) {
//                        if (!current.isWiFiSupported()) {
//                            isConnectionPossible = false;
//                            bottomSheetDialogToConnect.dismiss();
//                            Snackbar snackbar = Snackbar
//                                    .make(main_bottom_menu, getString(R.string.device_title) +
//                                                    " " + current.getDevName() + " " +
//                                                    getString(R.string.wifi_not_supported_text),
//                                            Snackbar.LENGTH_LONG)
//                                    .setAction(getString(R.string.button_more), new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_wifi));
//                                        }
//                                    });
//                            snackbar.show();
//                            break;
//                        }
//                    }
//                    if (isConnectionPossible) {
//                        Intent serviceStarted;
//                        serviceStarted = new Intent("startingWiFiService");
//                        sendBroadcast(serviceStarted);
//                        bottomSheetDialogToConnect.dismiss();
//                    }
//                } else {
//                    createOneButtonAlertDialog(getString(R.string.error), getString(R.string.suggestionNoWiFiAdapter));
//                }
//
//            });
//        }
//        if (buttonToConnectViaBt != null) {
//            buttonToConnectViaBt.setOnClickListener(view1 -> {
//                if (App.isBtSupported()) {
//                    boolean isConnectionPossible = true;
//                    for (DeviceModel current : App.getDevicesList()) {
//                        if (!current.isBtSupported()) {
//                            isConnectionPossible = false;
//                            bottomSheetDialogToConnect.dismiss();
//                            Snackbar snackbar = Snackbar
//                                    .make(main_bottom_menu, getString(R.string.device_title) +
//                                                    " " + current.getDevName() + " " +
//                                                    getString(R.string.bluetooth_not_supported_text),
//                                            Snackbar.LENGTH_LONG)
//                                    .setAction(getString(R.string.button_more), new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View view) {
//                                            createOneButtonAlertDialog(getString(R.string.alert_info), getString(R.string.instruction_for_app_bt));
//                                        }
//                                    });
//                            snackbar.show();
//                            break;
//                        }
//                    }
//                    if (isConnectionPossible) {
//                        Intent serviceStarted;
//                        serviceStarted = new Intent("startingBtService");
//                        sendBroadcast(serviceStarted);
//                        bottomSheetDialogToConnect.dismiss();
//                    }
//                } else {
//                    createOneButtonAlertDialog(getString(R.string.error), getString(R.string.suggestionNoBtAdapter));
//                }
//
//            });
//        }
        requestMultiplePermissions()
    }

    private fun setUpNavigation() {
        val main_bottom_menu = binding!!.bottomnav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        navHostFragment?.let { navHost ->
            setupWithNavController(
                main_bottom_menu,
                navHost.navController
            )

            navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
                Log.e(APP_LOG_TAG, "onDestinationChanged: " + destination.label)
                // Отслеживаем текущий фрагмент на главном экране
                viewModel.setCurrentVisibleFragment(destination)
            }

            main_bottom_menu.setOnItemSelectedListener { item ->
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
    fun checkForBtAdapter() {

//        if (!App.isBtWiFiSupported()) {
//            // объект Builder для создания диалогового окна
//            AlertDialog dialog = new MaterialAlertDialogBuilder(this,
//                    com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog).create();
//            dialog.setTitle(getString(R.string.error));
//            dialog.setMessage(getString(R.string.suggestionNoBtWiFiAdapter));
//            dialog.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
//                    (dialog1, which) -> {
//                        dialog1.dismiss();
//                    });
//            dialog.show();
//        }
    }

    public override fun onResume() {
        super.onResume()
        //        if (App.isActivityConnection()) {
//            App.setActivityConnectionState(false);
//        }
//        checkForBtAdapter();
    }

    private fun requestMultiplePermissions() {
        viewModel.requestPermissions()
        viewModel.permissionList.observe(this, androidx.lifecycle.Observer{ list ->
            if (list.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    this,
                    list.toTypedArray(),
                    PERMISSION_REQUEST_CODE
                )
            }
        })
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
        navController!!.navigate(R.id.oneButtonAlertDialogFragment, message)
    }

    @Synchronized
    fun showMainMenu() {
        main_bottom_menu!!.visibility = View.VISIBLE
    }

    @Synchronized
    fun hideMainMenu() {
        main_bottom_menu!!.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
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
    private val mMessageReceiverNeedToStartBtService: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//            isBtConnection = true;
//            if (App.isBtEnabled()) {
//                //TODO: стартовать соединение через новые классы, это удалить
//                //startConnectionService();
//            } else {
//                Bundle args = new Bundle();
//                args.putString("dialogTitle", getString(R.string.error));
//                args.putString("dialogText", getString(R.string.en_bt_for_connection));
//                navController.navigateUp();
//                navController.navigate(R.id.oneButtonAlertDialogFragment, args);
//            }
            }
        }
    private val mMessageReceiverNeedToStartWiFiService: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                isBtConnection = false
                //            if (App.isWiFiEnabled()) {
//                //TODO: стартовать соединение через новые классы, это удалить
//                //startConnectionService();
//            } else {
//                Bundle args = new Bundle();
//                args.putString("dialogTitle", getString(R.string.error));
//                args.putString("dialogText", getString(R.string.en_wifi_for_connection));
//                navController.navigateUp();
//                navController.navigate(R.id.oneButtonAlertDialogFragment, args);
//            }
            }
        }

    //Результат работы Service
    private val mMessageReceiverNotSuccess: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //TODO: обновить логику при неудачном соединении
//            navController.navigate(R.id.mainMenuFragment);
//            {
//                try {
//                    if (getMainMenuFragment() != null)
//                        getMainMenuFragment().onRefresh();
//                } catch (java.lang.IllegalStateException e) {
//                    //nothing
//                }
//            }
//            App.setServiceConnecting(false);
//            createOneButtonAlertDialog(getString(R.string.error), getString(R.string.connection_error));
//            isBtConnection = null;
        }
    }
    private val mMessageReceiverSuccess: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
//            if(!App.isActivityConnection()&& App.isServiceConnecting()){
//                //Устройство подключено, Service выполнился успешно
//                navController.navigate(R.id.mainMenuFragment);
//                Bundle b = new Bundle();
//                //TODO: обновить логику при удачном соединении
//                //b.putBoolean("isBtService", !App.getDevicesList().get(0).isWiFiBtConnected());
//                navController.navigate(R.id.connectionActivity, b);
//                isBtConnection = null;
//                App.setServiceConnecting(false);
//            }
        }
    }

    override fun onBackPressed() {
        if (currentVisibleFragment!!.id == R.id.mainMenuFragment && !main_bottom_menu!!.isShown) {
            if (mainMenuFragment != null) mainMenuFragment!!.onRefresh()
        } else if (currentVisibleFragment!!.id == R.id.mainMenuFragment) {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                finish()
            } else {
                //показ сообщения, о необходимости второго нажатия кнопки назад при выходе
                val snackbar = Snackbar
                    .make(
                        main_bottom_menu!!, getString(R.string.double_back_click),
                        Snackbar.LENGTH_LONG
                    )
                snackbar.show()
            }
            back_pressed = System.currentTimeMillis()
        } else {
            super.onBackPressed()
        }
    }

    fun enableNetwork() {
//        if (isBtConnection) {
//            BluetoothAdapter mBluetoothAdapter = App.getBtAdapter();
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
//                        Manifest.permission.BLUETOOTH_CONNECT
//                }, 2);
//                return;
//            }
//            mBluetoothAdapter.enable();
//        } else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                Intent panelIntent = new Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY);
//                this.startActivity(panelIntent);
//            } else {
//                WifiManager wifiManager = (WifiManager)
//                        getApplicationContext().getSystemService(WIFI_SERVICE);
//                wifiManager.setWifiEnabled(true);
//            }
//        }
    }

    override fun onDialogDismissed() {
        if (isBtConnection != null) {
            enableNetwork()
        }
    }

    val mainMenuFragment: MainMenuFragment?
        get() {
            if (currentVisibleFragment!!.id == R.id.mainMenuFragment) {
                var fragmentManager: FragmentManager? = null
                if (navHostFragment != null) {
                    fragmentManager = navHostFragment!!.childFragmentManager
                }
                var current: Fragment? = null
                if (fragmentManager != null) {
                    current = fragmentManager.primaryNavigationFragment
                }
                if (current is MainMenuFragment) {
                    return current
                }
            }
            return null
        }

    override fun startConnectionService(selectedDevices: List<DeviceModel>) {
//        if(!App.isServiceConnecting()){
//            App.setServiceConnecting(true);
//            //TODO: обновить логику старта соединения с новыми классами
//            //Intent startConnectionService = new Intent(App.getContext(), ConnectionService.class);
//            Bundle b = new Bundle();
//            b.putSerializable("deviceList", (Serializable) selectedDevices);
//            b.putBoolean("isBtService", isBtConnection);
////            startConnectionService.putExtra("bundle", b);
////            startService(startConnectionService);
//            navController.navigate(R.id.connection_dialog);
//        }
    }

    companion object {
        //Переменная для хранения времени между нажатиями кнопки назад
        private var back_pressed: Long = 0
    }
}