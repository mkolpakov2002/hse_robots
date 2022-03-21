package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLivePlaybackSpeedControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

import androidx.appcompat.widget.Toolbar;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private boolean isHoldCommand;
    private byte[] message;      // комманда посылаемая на arduino
    private byte prevCommand = 0;
    private ArrayList<ConnectionThread> dataThreadForArduinoList;
    private List<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> disconnectedDevicesList;
    private TextView outputText;
    ProtocolRepo protocolRepo;
    private int countCommands;
    private int lengthMes;
    private boolean active;
    private Resources res;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    Dialog disconnectedDialog;
    Dialog networkDialog;
    boolean isBtService;
    ArrayList<PlayerView> playerViews;
    LinearLayout videoLinearLayout;
    ScrollView videoScrollLayout;
    ScrollView buttonScrollLayout;

    public void showAlertWithOneButton(){
        MaterialAlertDialogBuilder alertDialog = new MaterialAlertDialogBuilder(ConnectionActivity.this);
        alertDialog.setTitle(getString(R.string.instruction_alert))
                   .setMessage(getString(R.string.instruction_for_app_connection_activity))
                   .setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_connection);
        App.setActivityConnectionState(true);

        MaterialToolbar toolbar = findViewById(R.id.toolbar_connection_activity);
        toolbar.inflateMenu(R.menu.main_toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.main_toolbar_instruction) {
                  showAlertWithOneButton();
                }
                return false;
            }
        });

        Bundle b = getIntent().getExtras();
        isBtService = b.getBoolean("isBtService");

        disconnectedDevicesList = new ArrayList<>();
        devicesList = new ArrayList<>();
        devicesList = App.getDevicesList();
        checkForActiveDevices();
        if(devicesList.size()>0){
            registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
            registerReceiver(mReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
            String devProtocol = devicesList.get(0).getDevProtocol();
            findViewById(R.id.button_stop_bt).setEnabled(false);
            outputText = findViewById(R.id.incoming_data_bt);
            outputText.setMovementMethod(new ScrollingMovementMethod());

            dataThreadForArduinoList = new ArrayList<>();
            outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_connected_first) + " " + devicesList.size() + " " + getResources().getString(R.string.bluetooth_device_activity_from) + " " + (devicesList.size() + disconnectedDevicesList.size()) + " " + getResources().getString(R.string.devices_title));
            outputText.append("\n" + getResources().getString(R.string.bluetooth_device_activity_list_of_connections));
            for (int i = 0; i < devicesList.size(); i++) {
                outputText.append("\n" + getResources().getString(R.string.device_title) + " " + devicesList.get(i).getDevName() + " " + getResources().getString(R.string.bluetooth_device_activity_connected_second));
                ConnectionThread bluetoothDataThreadForArduino = new ConnectionThread(this, devicesList.get(i), isBtService);
                dataThreadForArduinoList.add(bluetoothDataThreadForArduino);
                dataThreadForArduinoList.get(i).start();
            }

            res = getResources();
            isHoldCommand = false;

            protocolRepo = new ProtocolRepo(getApplicationContext(), devProtocol);
            ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
            lengthMes = protocolDBHelper.getLength(devProtocol);
            message = new byte[lengthMes];
            countCommands = 0;

            findViewById(R.id.button_up_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_down_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_left_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_right_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_stop_bt).setOnClickListener(this);

            SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm_Bt);
            hold_command.setOnCheckedChangeListener(this);
            hold_command.setChecked(false);
            Arrays.fill(message, (byte) 0);

            videoLinearLayout = findViewById(R.id.gridLayout);
            videoScrollLayout = findViewById(R.id.scrollViewVideo);
            buttonScrollLayout = findViewById(R.id.scrollView5);
            //TODO
            //https://exoplayer.dev/hello-world.html
            //https://medium.com/mindorks/implementing-exoplayer-for-beginners-in-kotlin-c534706bce4b
            if(!isBtService && protocolRepo.isCameraSupported()){
                playerViews = new ArrayList<>();
                initializePlayer();
            } else {
                videoScrollLayout.setVisibility(View.GONE);
            }
            if(!protocolRepo.isMoveSupported()){
                findViewById(R.id.constraintLayout2).setVisibility(View.GONE);
            }
            if(protocolRepo.isNeedNewCommandButton()){
                createButtonList();
            } else {
                buttonScrollLayout.setVisibility(View.GONE);
            }
        } else {
            addDisconnectedDevice();
        }

        //TODO
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //this.registerReceiver(mReceiver3, filter3);

        ConnectionActivity activity = this;
        MaterialButton stringButton = findViewById(R.id.button_send_string);
        stringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(activity, R.style.AlertDialog_AppCompat).create();
                dialog.setTitle("Input string message");

                final EditText input = new EditText(activity);
                input.setHint("Message");
                input.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
                input.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
                input.setMinimumHeight(50);
                input.setGravity(Gravity.START);
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                dialog.setView(input);
                dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                            dataThreadForArduinoList.get(i).sendData(input.getText().toString().trim());
                        }
                        dialog.dismiss();
                    }
                });
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            if((isBtService && !App.isBtEnabled()) ||
                    (!isBtService && !App.isWiFiEnabled())){
                addDisconnectedDevice();
            }
        }};

    public synchronized void printDataToTextView(String printData) {
        Log.d(APP_LOG_TAG, "Печатаемое сообщение в DeviceActivity: " + printData);
        outputText.append("\n" + "---" + "\n" + printData);
    }

    public synchronized boolean isActive() {
        return active;
    }

    synchronized void checkForActiveDevices() {
        boolean hasChanges = false;
        for (DeviceItemType currentDevice : devicesList) {
            if (currentDevice.isWiFiBtConnected()==null) {
                disconnectedDevicesList.add(currentDevice);
                hasChanges = true;
            }
        }
        for (DeviceItemType currentDevice : disconnectedDevicesList) {
            if (currentDevice.isWiFiBtConnected()!=null) {
                devicesList.add(currentDevice);
                hasChanges = true;
            }
        }
        devicesList.removeIf(currentDevice -> (currentDevice.isWiFiBtConnected()==null));
        disconnectedDevicesList.removeIf(currentDevice -> (currentDevice.isWiFiBtConnected()!=null));
        if(hasChanges){
            addDisconnectedDevice();
        }
    }

    public synchronized void addDisconnectedDevice() {
        if(isActive()) {
            if ((disconnectedDialog == null || !disconnectedDialog.isShowing())
                    && ((isBtService && App.isBtEnabled()) ||
                    (!isBtService && App.isWiFiEnabled())) && devicesList.size() > 0) {
                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                materialAlertDialogBuilder.setTitle(getString(R.string.error));
                if (disconnectedDevicesList.size() == 1) {
                    materialAlertDialogBuilder.setMessage("Устройство " + disconnectedDevicesList.get(0).getDevName() + "отключилось. Продолжить работу?");
                } else {
                    materialAlertDialogBuilder.setMessage("Некоторые устройства отключились. Продолжить работу?");
                }
                materialAlertDialogBuilder.setPositiveButton("Продолжить работу", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                materialAlertDialogBuilder.setNegativeButton("Выйти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                disconnectedDialog = materialAlertDialogBuilder.show();
            } else if ((disconnectedDialog == null || !disconnectedDialog.isShowing())
                    && ((isBtService && App.isBtEnabled()) ||
                    (!isBtService && App.isWiFiEnabled()))) {
                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                materialAlertDialogBuilder.setTitle(getString(R.string.error));
                materialAlertDialogBuilder.setMessage("Все устройства отключены. Дальнейшее управление невозможно.");
                materialAlertDialogBuilder.setPositiveButton("Переподключиться", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        App.setServiceConnecting(true);
                        App.setDevicesList(disconnectedDevicesList);
                        Intent startConnectionService = new Intent(App.getContext(), ConnectionService.class);
                        startConnectionService.putExtra("isBtService", isBtService);
                        startService(startConnectionService);
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                materialAlertDialogBuilder.setNegativeButton("Выход", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                materialAlertDialogBuilder.setCancelable(false);
                disconnectedDialog = networkDialog = materialAlertDialogBuilder.show();
            } else if (((isBtService && !App.isBtEnabled()) || (!isBtService && !App.isWiFiEnabled()))
                    && (networkDialog == null || !networkDialog.isShowing())) {
                if (networkDialog != null && disconnectedDialog.isShowing())
                    disconnectedDialog.dismiss();
                materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
                materialAlertDialogBuilder.setTitle(getString(R.string.error));
                materialAlertDialogBuilder.setMessage("Сеть отключена. Дальнейшее управление невозможно.");
                materialAlertDialogBuilder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        finish();
                    }
                });
                materialAlertDialogBuilder.setCancelable(false);
                networkDialog = materialAlertDialogBuilder.show();
            } else {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        checkForActiveDevices();
        if(!isBtService && protocolRepo.isCameraSupported()){
            initializePlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(APP_LOG_TAG, "DeviceActivity в onPause");
        active = false;
        if(!isBtService && protocolRepo.isCameraSupported())
            releasePlayer();
        checkForActiveDevices();
        if(devicesList.size()>0){
            completeDevicesInfo();
            if (protocolRepo.getTag(res.getString(R.string.TAG_TURN_COM)))
                message[countCommands++] = protocolRepo.get("new_command");

            if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_COM)))
                message[countCommands++] = protocolRepo.get("type_move");

            message[countCommands++] = protocolRepo.get("STOP");
            for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                dataThreadForArduinoList.get(i).sendData(message, lengthMes);
            }

            for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                dataThreadForArduinoList.get(i).Disconnect();
            }
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        active = false;
    }

    @Override
    public void onClick(View v) {
        completeDevicesInfo();
        switch (v.getId()) {
            case (R.id.button_stop_bt):
                outputText.append("\n" + getResources().getString(R.string.send_command_stop));
                completeMessage("STOP");
                countCommands = 0;
                break;
        }
    }



    View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            completeDevicesInfo();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // если нажали на кнопку и не важно есть удержание команд или нет
                switch (v.getId()) {
                    case (R.id.button_up_bt):
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вперёд;");
                        outputText.append("\n" + getResources().getString(R.string.send_command_forward));
                        completeMessage("FORWARD");
                        countCommands = 0;
                        break;
                    case (R.id.button_down_bt):
                        outputText.append("\n" + getResources().getString(R.string.send_command_back));
                        Log.d(APP_LOG_TAG, "Отправляю команду движения назад;");
                        //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                        completeMessage("BACK");
                        countCommands = 0;
                        break;
                    case (R.id.button_left_bt):
                        outputText.append("\n" + getResources().getString(R.string.send_command_left));
                        //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                        Log.d(APP_LOG_TAG, "Отправляю команду движения влево;");
                        completeMessage("LEFT");
                        countCommands = 0;
                        break;
                    case (R.id.button_right_bt):
                        //Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                        outputText.append("\n" + getResources().getString(R.string.send_command_right));
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вправо;");
                        completeMessage("RIGHT");
                        countCommands = 0;
                        break;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                // если отпустили кнопку
                if (!isHoldCommand) {
                    // и нет удержания команд то все кнопки отправляют команду стоп
                    outputText.append("\n" + getResources().getString(R.string.send_command_button_released));
                    switch (v.getId()) {
                        case (R.id.button_up_bt):
                            completeMessage("FORWARD_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_down_bt):
                            completeMessage("BACK_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_left_bt):
                            completeMessage("LEFT_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_right_bt):
                            completeMessage("RIGHT_STOP");
                            countCommands = 0;
                            break;
                    }
                    Log.d(APP_LOG_TAG, "Количество посылаемых команд " + countCommands);
                }
            }
            return false;
        }
    };

    public void completeDevicesInfo() {
        countCommands = 0;
        if (protocolRepo.getTag(res.getString(R.string.TAG_CLASS_FROM)))
            message[countCommands++] = protocolRepo.get("class_android");

        if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_FROM)))
            message[countCommands++] = protocolRepo.get("type_computer"); // класс и тип устройства отправки

        if (protocolRepo.getTag(res.getString(R.string.TAG_CLASS_TO)))
            message[countCommands++] = protocolRepo.get(devicesList.get(0).getDevClass());

        if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_TO)))
            message[countCommands++] = protocolRepo.get(devicesList.get(0).getDevType());// класс и тип устройства приема
    }

    public void completeMessage(String command) {

        Byte code = protocolRepo.get(command);
        if (code != null) {
            if (protocolRepo.getTag(res.getString(R.string.TAG_TURN_COM))) {
                message[countCommands++] = (prevCommand == code) ? protocolRepo.get("redo_command") : protocolRepo.get("new_command");
                prevCommand = code;
            }

            if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_COM)))
                message[countCommands++] = protocolRepo.get("type_move");
            message[countCommands++] = code;

            for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                dataThreadForArduinoList.get(i).sendData(message, lengthMes);
            }
        } else {
            outputText.append("\n" + getResources().getString(R.string.send_command_insufficient_data));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_hold_command_mm_Bt:
                isHoldCommand = isChecked;
                if (isHoldCommand) {
                    outputText.append("\n" + getResources().getString(R.string.send_command_hold_enabled));
                    findViewById(R.id.button_stop_bt).setEnabled(true);
                } else {
                    outputText.append("\n" + getResources().getString(R.string.send_command_hold_disabled));
                    findViewById(R.id.button_stop_bt).setEnabled(false);
                }
                break;
        }
    }


    // Метод для вывода всплывающих данных на экран
    public void showToast(String outputInfoString) {
        Toast outputInfoToast = Toast.makeText(this, outputInfoString, Toast.LENGTH_SHORT);
        outputInfoToast.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        active = false;
        for (int i = 0; i < devicesList.size(); i++) {
            Log.d(APP_LOG_TAG, "DeviceActivity в onDestroy, отключение устройств");
            devicesList.get(i).closeConnection();
        }
    }

    private void createButtonList(){
        LinearLayout buttonListLayout;
        GridLayout buttonGridLayout;
        if(protocolRepo.isCameraSupported() && protocolRepo.isMoveSupported()) {
            buttonListLayout = new LinearLayout(this);
            buttonListLayout.setOrientation(LinearLayout.VERTICAL);
            for(String command: protocolRepo.getNewDynamicCommands().keySet()){
                MaterialButton currButton = createCommandButton(command);
                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                currButton.setLayoutParams(param);
                buttonListLayout.addView(currButton);
            }

            buttonScrollLayout.addView(buttonListLayout);
        } else if(protocolRepo.isCameraSupported() || protocolRepo.isMoveSupported()) {
            buttonGridLayout = new GridLayout(this);
            buttonGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            buttonGridLayout.setColumnCount(2);

            int currentCol = 0;
            int currentRow = 0;

            int paddingDp = 8;
            float density = this.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);

            for(String command: protocolRepo.getNewDynamicCommands().keySet()){
                MaterialButton currButton = createCommandButton(command);
                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.columnSpec = GridLayout.spec(currentCol, 1, 1);
                param.rowSpec = GridLayout.spec(currentRow, 1, 1);
                param.setMargins(paddingPixel, 0, paddingPixel, paddingPixel);
                currButton.setLayoutParams(param);
                buttonGridLayout.addView(currButton);
                currentCol++;
                if(currentCol == 2){
                    currentCol = 0;
                    currentRow++;
                }
            }
            buttonScrollLayout.addView(buttonGridLayout);
        } else {
            buttonGridLayout = new GridLayout(this);
            buttonGridLayout.setAlignmentMode(GridLayout.ALIGN_BOUNDS);
            buttonGridLayout.setColumnCount(3);

            int paddingDp = 8;
            float density = this.getResources().getDisplayMetrics().density;
            int paddingPixel = (int)(paddingDp * density);


            int currentCol = 0;
            int currentRow = 0;
            for(String command: protocolRepo.getNewDynamicCommands().keySet()){

                MaterialButton currButton = createCommandButton(command);
                GridLayout.LayoutParams param =new GridLayout.LayoutParams();
                param.height = GridLayout.LayoutParams.WRAP_CONTENT;
                param.width = GridLayout.LayoutParams.WRAP_CONTENT;
                param.columnSpec = GridLayout.spec(currentCol, 1, 1);
                param.rowSpec = GridLayout.spec(currentRow, 1, 1);
                param.setMargins(paddingPixel, 0, paddingPixel, paddingPixel);
                currButton.setLayoutParams(param);
                buttonGridLayout.addView(currButton);
                currentCol++;
                if(currentCol == 3){
                    currentCol = 0;
                    currentRow++;
                }
            }

            buttonScrollLayout.addView(buttonGridLayout);

        }

    }

    private MaterialButton createCommandButton(String command){
        MaterialButton curButton = new MaterialButton(this, null, R.attr.materialButtonStyle);

        curButton.setText(command);
        curButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeMessage(command);
                countCommands = 0;
                outputText.append("\n" + "Отправляю команду " + command);
            }
        });
        return curButton;

    }

    private void initializePlayer(){
        playerViews.clear();
        for(int i = 0; i < devicesList.size(); i++){
            ExoPlayer player = new ExoPlayer.Builder(this)
                    .setLivePlaybackSpeedControl(
                    new DefaultLivePlaybackSpeedControl.Builder()
                            .setFallbackMaxPlaybackSpeed(1.04f)
                            .build()).build();
            PlayerView playerView = new PlayerView(this);
            playerView.setPlayer(player);
            playerView.setKeepScreenOn(true);
            playerView.setMinimumHeight(300);
            playerView.setUseController(false);
            videoLinearLayout.addView(playerView);
            MediaItem item = new MediaItem.Builder()
                    .setUri(devicesList.get(i).getDevIp())
                    .build();
            // Set the media items to be played.
            player.setMediaItem(item);
            // Prepare the player.
            player.prepare();
            // Start the playback.
            player.play();
            playerViews.add(playerView);
        }
    }

    private void releasePlayer(){
        for(int i = 0; i < devicesList.size(); i++){
            if(playerViews.get(i)!=null){
                Objects.requireNonNull(playerViews.get(i).getPlayer()).stop();
                Objects.requireNonNull(playerViews.get(i).getPlayer()).release();
            }
        }
        playerViews.clear();
    }
}
