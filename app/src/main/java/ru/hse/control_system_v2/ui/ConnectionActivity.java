package ru.hse.control_system_v2.ui;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.DefaultLivePlaybackSpeedControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.ThemeUtils;
import ru.hse.control_system_v2.connection.ConnectionService;
import ru.hse.control_system_v2.connection.ConnectionThread;
import ru.hse.control_system_v2.data.ProtocolDBHelper;
import ru.hse.control_system_v2.data.ProtocolRepo;
import ru.hse.control_system_v2.data.DeviceItemType;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private boolean isHoldCommand;
    private byte[] sendingToDeviceMessage;      // комманда посылаемая на arduino
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
    LinearLayout videoLinearLayout;
    ScrollView videoScrollLayout;
    ScrollView buttonScrollLayout;
    int playerId = 0;

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
        } else ThemeUtils.onActivityCreateSetTheme(this);
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
            //TODO
            //Мурату
            ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
            lengthMes = protocolDBHelper.getLength(devProtocol);
            sendingToDeviceMessage = new byte[lengthMes];
            countCommands = 0;

            findViewById(R.id.button_up_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_down_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_left_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_right_bt).setOnTouchListener(touchListener);
            findViewById(R.id.button_stop_bt).setOnClickListener(this);

            SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm_Bt);
            hold_command.setOnCheckedChangeListener(this);
            hold_command.setChecked(false);
            Arrays.fill(sendingToDeviceMessage, (byte) 0);

            videoLinearLayout = findViewById(R.id.gridLayout);
            videoScrollLayout = findViewById(R.id.scrollViewVideo);
            buttonScrollLayout = findViewById(R.id.scrollView5);
            //TODO
            //https://exoplayer.dev/hello-world.html
            //https://medium.com/mindorks/implementing-exoplayer-for-beginners-in-kotlin-c534706bce4b
            if(!isBtService && protocolRepo.isCameraSupported()){
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
            showDialogError(false);
        }

        //TODO
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //this.registerReceiver(mReceiver3, filter3);

        ConnectionActivity activity = this;
        MaterialButton stringButton = findViewById(R.id.button_send_string);
        stringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialoglayout = inflater.inflate(R.layout.dialog_string_message, null);
                AlertDialog dialog = new MaterialAlertDialogBuilder(activity,
                        com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog).create();
                dialog.setView(dialoglayout);
                EditText input = dialoglayout.findViewById(R.id.stringMessageEditText);

                MaterialButton sendButton = dialoglayout.findViewById(R.id.buttonStringSend);
                sendButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mes = input.getText().toString().trim();
                        outputText.append("\n" + getString(R.string.send_command_title) + mes);
                        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                            dataThreadForArduinoList.get(i).sendData(mes);
                        }
                        dialog.dismiss();
                    }
                });

                MaterialButton dismissButton = dialoglayout.findViewById(R.id.buttonStringCancel);
                dismissButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
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
                showDialogError(false);
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
            showDialogError(false);
        }
    }

    private boolean isDialogNotCancelable = false;

    public void showDialogError(boolean isVideoError, String... devName){
        if(isActive() && !isDialogNotCancelable) {
            if (disconnectedDialog != null && disconnectedDialog.isShowing()){
                disconnectedDialog.dismiss();
            }
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle(getString(R.string.error));
            materialAlertDialogBuilder.setNegativeButton(getString(R.string.exit), (dialogInterface, i) -> {
                dialogInterface.dismiss();
                finish();
            });

            materialAlertDialogBuilder.setPositiveButton(getString(R.string.continue_work), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            if(isVideoError){
                materialAlertDialogBuilder.setMessage(getString(R.string.video_stream_failed)+Arrays.toString(devName));
            } else {
                if (((isBtService && App.isBtEnabled()) ||
                        (!isBtService && App.isWiFiEnabled()))){
                    if (disconnectedDevicesList.size()==1 && devicesList.size()>0) {
                        materialAlertDialogBuilder.setMessage(disconnectedDevicesList.get(0).getDevName()
                                + getString(R.string.one_dev_disconnected)
                                + getString(R.string.continue_work_question));
                    } else if (devicesList.size()==0) {
                        materialAlertDialogBuilder.setMessage(getString(R.string.all_dev_disconnected));
                        materialAlertDialogBuilder.setPositiveButton(getString(R.string.reconnect), new DialogInterface.OnClickListener() {
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

                        materialAlertDialogBuilder.setCancelable(false);
                        isDialogNotCancelable = true;
                    } else {
                        materialAlertDialogBuilder.setMessage(getString(R.string.some_dev_disconnected)
                                + getString(R.string.continue_work_question));
                    }
                } else {
                    materialAlertDialogBuilder.setMessage(getString(R.string.network_disconnected_in_connection));
                    materialAlertDialogBuilder.setPositiveButton(getString(R.string.reconnect), new DialogInterface.OnClickListener() {
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
                    materialAlertDialogBuilder.setCancelable(false);
                    isDialogNotCancelable = true;
                }
            }
            disconnectedDialog = materialAlertDialogBuilder.show();
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
            if(protocolRepo.isNeedPackageData()){
                completeDevicesInfo();
                if (protocolRepo.getTag(res.getString(R.string.TAG_TURN_COM)))
                    sendingToDeviceMessage[countCommands++] = protocolRepo.get("new_command");

                if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_COM)))
                    sendingToDeviceMessage[countCommands++] = protocolRepo.get("type_move");

                sendingToDeviceMessage[countCommands++] = protocolRepo.get("STOP");
                for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                    dataThreadForArduinoList.get(i).sendData(sendingToDeviceMessage, lengthMes);
                }

            } else {
                for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                    dataThreadForArduinoList.get(i).sendData(protocolRepo.get("STOP"));
                }
            }
            for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                dataThreadForArduinoList.get(i).disconnectDevice();
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
                outputText.append("\n" + getResources().getString(R.string.send_command_title));
                // если нажали на кнопку и не важно есть удержание команд или нет
                switch (v.getId()) {
                    case (R.id.button_up_bt):
                        Log.d(APP_LOG_TAG, "Отправляю команду движения вперёд;");
                        outputText.append(getResources().getString(R.string.send_command_forward));
                        completeMessage("FORWARD");
                        countCommands = 0;
                        break;
                    case (R.id.button_down_bt):
                        outputText.append(getResources().getString(R.string.send_command_back));
                        Log.d(APP_LOG_TAG, "Отправляю команду движения назад;");
                        //Toast.makeText(getApplicationContext(), "Назад поехали", Toast.LENGTH_SHORT).show();
                        completeMessage("BACK");
                        countCommands = 0;
                        break;
                    case (R.id.button_left_bt):
                        outputText.append(getResources().getString(R.string.send_command_left));
                        //Toast.makeText(getApplicationContext(), "Влево поехали", Toast.LENGTH_SHORT).show();
                        Log.d(APP_LOG_TAG, "Отправляю команду движения влево;");
                        completeMessage("LEFT");
                        countCommands = 0;
                        break;
                    case (R.id.button_right_bt):
                        //Toast.makeText(getApplicationContext(), "Вправо поехали", Toast.LENGTH_SHORT).show();
                        outputText.append(getResources().getString(R.string.send_command_right));
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
            sendingToDeviceMessage[countCommands++] = protocolRepo.get("class_android");

        if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_FROM)))
            sendingToDeviceMessage[countCommands++] = protocolRepo.get("type_computer"); // класс и тип устройства отправки

        if (protocolRepo.getTag(res.getString(R.string.TAG_CLASS_TO)))
            sendingToDeviceMessage[countCommands++] = protocolRepo.get(devicesList.get(0).getDevClass());

        if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_TO)))
            sendingToDeviceMessage[countCommands++] = protocolRepo.get(devicesList.get(0).getDevType());// класс и тип устройства приема
    }

    public void completeMessage(String command) {

        Byte code = protocolRepo.get(command);

        if(protocolRepo.isNeedPackageData()){
            if (code != null) {
                if (protocolRepo.getTag(res.getString(R.string.TAG_TURN_COM))) {
                    sendingToDeviceMessage[countCommands++] = (prevCommand == code) ? protocolRepo.get("redo_command") : protocolRepo.get("new_command");
                    prevCommand = code;
                }

                if (protocolRepo.getTag(res.getString(R.string.TAG_TYPE_COM)))
                    sendingToDeviceMessage[countCommands++] = protocolRepo.get("type_move");
                sendingToDeviceMessage[countCommands++] = code;

                for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                    dataThreadForArduinoList.get(i).sendData(sendingToDeviceMessage, lengthMes);
                }
            } else {
                outputText.append("\n" + getResources().getString(R.string.send_command_insufficient_data));
            }
        } else {
            if (code != null) {
                for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
                    dataThreadForArduinoList.get(i).sendData(code);
                }
            }  else {
                outputText.append("\n" + getResources().getString(R.string.send_command_insufficient_data));
            }
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
        GridLayout buttonGridLayout;
        buttonGridLayout = new GridLayout(this);
        buttonGridLayout.setUseDefaultMargins(true);
        if(!isBtService && protocolRepo.isCameraSupported()) {
            buttonGridLayout.setColumnCount(1);
        } else {
            buttonGridLayout.setColumnCount(2);
        }
        int currentCol = 0;
        int currentRow = 0;

        for(String command: protocolRepo.getNewDynamicCommands().keySet()){
            MaterialButton currButton = createCommandButton(command);
            GridLayout.LayoutParams param =new GridLayout.LayoutParams();
            param.height = GridLayout.LayoutParams.WRAP_CONTENT;
            param.width = GridLayout.LayoutParams.WRAP_CONTENT;
            if(buttonGridLayout.getColumnCount()==1)
                param.columnSpec = GridLayout.spec(0, 1, 1);
            else param.columnSpec = GridLayout.spec(currentCol, 1, 1);
            param.rowSpec = GridLayout.spec(currentRow, 1, 1);
            currButton.setLayoutParams(param);
            buttonGridLayout.addView(currButton);
            if(buttonGridLayout.getColumnCount()==1){
                currentRow++;
            } else {
                currentCol++;
                if(currentCol == 2){
                    currentCol = 0;
                    currentRow++;
                }
            }
        }
        buttonScrollLayout.addView(buttonGridLayout);
    }

    private MaterialButton createCommandButton(String command){
        MaterialButton curButton = new MaterialButton(this, null, R.attr.materialButtonStyle);
        curButton.setText(command);
        curButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                completeMessage(command);
                countCommands = 0;
                outputText.append("\n" + getString(R.string.send_command_title) + command);
            }
        });
        return curButton;
    }

    private void initializePlayer(){
        videoLinearLayout.removeAllViewsInLayout();
        for(int i = 0; i < devicesList.size(); i++){
            if(devicesList.get(i).isExoPlayerVideo()){
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
                Uri uri = Uri.parse("http://"+devicesList.get(i).getDevIp() + ":"
                        + devicesList.get(i).getDevPort() + "/"
                        + devicesList.get(i).getDevVideoCommand());

            MediaItem item = new MediaItem.Builder()
                    .setUri(uri)
                    .build();

                // Set the media items to be played.
                player.setMediaItem(item);
                // Prepare the player.
                player.prepare();
                // Start the playback.
                player.play();
                int finalI = i;
                player.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerError(@NonNull PlaybackException error) {
                        Player.Listener.super.onPlayerError(error);
                        devicesList.get(finalI).setExoPlayerVideo(false);
                        devicesList.get(finalI).setExoVideoInError(true);
                        refreshPlayers();
                    }
                });
                playerView.setTag(devicesList.get(i).getDevId());
                playerId++;
                devicesList.get(i).setPlayerView(playerView);
                videoLinearLayout.addView(playerView);
            } else {
                connectWebSocket(i);
            }
        }
    }

    private synchronized void refreshPlayers(){
        for(int i = 0; i < devicesList.size(); i++){
            if(videoLinearLayout.findViewWithTag(devicesList.get(i).getDevId()) instanceof PlayerView
                    && devicesList.get(i).isExoVideoInError()){
                videoLinearLayout.removeViewInLayout(videoLinearLayout.findViewWithTag(devicesList.get(i).getDevId()));
                Objects.requireNonNull(devicesList.get(i).getPlayerView().getPlayer()).release();
                connectWebSocket(i);
            } else if(videoLinearLayout.findViewWithTag(devicesList.get(i).getDevId()) instanceof ImageView &&
                    devicesList.get(i).isSocketVideoInError()){
                videoLinearLayout.removeViewInLayout(videoLinearLayout.findViewWithTag(devicesList.get(i).getDevId()));
                showToast(getString(R.string.video_stream_failed)
                        + devicesList.get(i).getDevName());
                devicesList.get(i).getmWebSocketClient().close();
                showDialogError(true, devicesList.get(i).getDevName());
            }
        }
    }

    private void connectWebSocket(int i) {
        ConnectionActivity ca = this;

        URI uri;
        try {
            uri = new URI(Uri.parse(("ws://"+devicesList.get(i).getDevIp() + ":"
                    + devicesList.get(i).getDevPort() + "/"
                    + devicesList.get(i).getDevVideoCommand()).trim()).toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        devicesList.get(i).setmWebSocketClient(new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.e("Websocket", "Open");
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.e("Websocket", "Closed " + s);
            }

            @Override
            public void onMessage(String message){
                Log.e("Websocket", "Receive");
            }

            @Override
            public void onMessage(ByteBuffer message){
                ca.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Websocket", "RUN!!! ");
                        byte[] imageBytes= new byte[message.remaining()];
                        message.get(imageBytes);
                        final Bitmap bmp=BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                        if (bmp == null) {
                            return;
                        }
                        Matrix matrix = new Matrix();
                        matrix.postRotate(90);
                        final Bitmap bmp_traspose = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                        ImageView currentView = new ImageView(ca);
                        currentView.setTag(devicesList.get(i).getDevId());
                        currentView.setImageBitmap(Bitmap.createScaledBitmap(bmp_traspose, bmp_traspose.getWidth(), bmp_traspose.getHeight(), false));
                        devicesList.get(i).setPlayerImage(currentView);
                        if(videoLinearLayout.findViewWithTag(devicesList.get(i).getDevId())==null){
                            videoLinearLayout.addView(currentView);
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Log.e("Websocket", "Error " + e.getMessage());
                devicesList.get(i).setSocketVideoInError(true);
            }
        });
        devicesList.get(i).getmWebSocketClient().connect();

    }

    private void releasePlayer(){
        for(int i = 0; i < devicesList.size(); i++){
            Player curr = devicesList.get(i).getPlayerView().getPlayer();
            if(devicesList.get(i).isExoPlayerVideo() && curr!=null &&
                    !devicesList.get(i).isExoVideoInError()){
                curr.stop();
                curr.release();
            } else if (!devicesList.get(i).isExoPlayerVideo() &&
                    !devicesList.get(i).isSocketVideoInError()){
                devicesList.get(i).getmWebSocketClient().close();
            }
        }
        videoLinearLayout.removeAllViewsInLayout();
    }
}
