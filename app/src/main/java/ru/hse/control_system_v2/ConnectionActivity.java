package ru.hse.control_system_v2;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;
import ru.hse.control_system_v2.list_devices.DeviceItemType;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Objects;

public class ConnectionActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, TextureView.SurfaceTextureListener {

    // 1
//    final int CAMERA_REQUEST = 1;
//    final int PIC_CROP = 2;
//    private static final int REQUEST_TAKE_PHOTO = 1;
//    private static final int REQUEST_TAKE_VIDEO = 2;
//    private Uri picUri;
//    private Button btnCamera;
//    private TextureView mTextureView;

    private boolean isHoldCommand;
    private byte[] message;      // комманда посылаемая на arduino
    private byte prevCommand = 0;
    private ArrayList<ConnectionThread> dataThreadForArduinoList;
    private List<DeviceItemType> devicesList;
    private ArrayList<DeviceItemType> disconnectedDevicesList;
    private TextView outputText;
    ProtocolRepo getDevicesID;
    private int countCommands;
    private int lengthMes;
    private boolean active;
    private Resources res;
    MaterialAlertDialogBuilder materialAlertDialogBuilder;
    Dialog disconnectedDialog;
    Dialog networkDialog;
    boolean isBtService;

    public static final String LOG_TAG = "myLogs";

    CameraService[] myCameras = null;

    private CameraManager mCameraManager = null;
    private final int CAMERA1 = 0;
    private final int CAMERA2 = 1;

    private Button mButtonOpenCamera1 = null;
    private Button mButtonOpenCamera2 = null;
    private Button mButtonToMakeShot = null;
    private TextureView mImageView = null;
    private HandlerThread mBackgroundThread;
    private Handler mBackgroundHandler = null;
    private NavController navController;

    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        Log.d(LOG_TAG, "startBackgroundThread");
    }

    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showAlertWithOneButton(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConnectionActivity.this);
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


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_connection);

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
        devicesList = DeviceHandler.getDevicesList();
        checkForActiveDevices();
        if(devicesList.size()>0){
            initializeData();
        } else {
            addDisconnectedDevice();
        }


        Log.d(LOG_TAG, "Запрашиваем разрешение");
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                (ContextCompat.checkSelfPermission(
                        ConnectionActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        mButtonOpenCamera1 =  findViewById(R.id.button1);
        mButtonOpenCamera2 =  findViewById(R.id.button2);
        mButtonToMakeShot =findViewById(R.id.button3);
        mImageView = findViewById(R.id.textureView);

        mButtonOpenCamera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCameras[CAMERA2].isOpen()) {myCameras[CAMERA2].closeCamera();}
                if (myCameras[CAMERA1] != null) {
                    if (!myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].openCamera();
                }
            }
        });

        mButtonOpenCamera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCameras[CAMERA1].isOpen()) {myCameras[CAMERA1].closeCamera();}
                if (myCameras[CAMERA2] != null) {
                    if (!myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].openCamera();
                }
            }
        });

        mButtonToMakeShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myCameras[CAMERA1].isOpen()) myCameras[CAMERA1].makePhoto();
                if (myCameras[CAMERA2].isOpen()) myCameras[CAMERA2].makePhoto();
            }
        });

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try{
            // Получение списка камер с устройства
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];

            for (String cameraID : mCameraManager.getCameraIdList()) {
                Log.i(LOG_TAG, "cameraID: "+cameraID);
                int id = Integer.parseInt(cameraID);
                // создаем обработчик для камеры
                myCameras[id] = new CameraService(mCameraManager,cameraID);
            }
        }
        catch(CameraAccessException e){
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public class CameraService {

        private File mFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "test1.jpg");;
        private String mCameraID;
        private CameraDevice mCameraDevice = null;
        private CameraCaptureSession mCaptureSession;
        private ImageReader mImageReader;

        public CameraService(CameraManager cameraManager, String cameraID) {
            mCameraManager = cameraManager;
            mCameraID = cameraID;
        }

        public void makePhoto (){
            try {
                // This is the CaptureRequest.Builder that we use to take a picture.
                final CaptureRequest.Builder captureBuilder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
                captureBuilder.addTarget(mImageReader.getSurface());
                CameraCaptureSession.CaptureCallback CaptureCallback = new CameraCaptureSession.CaptureCallback() {
                    @Override
                    public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                                   @NonNull CaptureRequest request,
                                                   @NonNull TotalCaptureResult result) {

                    }
                };

                mCaptureSession.stopRepeating();
                mCaptureSession.abortCaptures();
                mCaptureSession.capture(captureBuilder.build(), CaptureCallback, mBackgroundHandler);
            }
            catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }


        private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
                = new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
            }
        };


        private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
            @Override
            public void onOpened(CameraDevice camera) {
                mCameraDevice = camera;
                Log.i(LOG_TAG, "Open camera  with id:"+mCameraDevice.getId());
                createCameraPreviewSession();
            }

            @Override
            public void onDisconnected(CameraDevice camera) {
                mCameraDevice.close();
                Log.i(LOG_TAG, "disconnect camera  with id:"+mCameraDevice.getId());
                mCameraDevice = null;
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Log.i(LOG_TAG, "error! camera id:"+camera.getId()+" error:"+error);
            }
        };

        private void createCameraPreviewSession() {

            mImageReader = ImageReader.newInstance(1920,1080, ImageFormat.JPEG,1);
            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, null);

            SurfaceTexture texture = mImageView.getSurfaceTexture();

            texture.setDefaultBufferSize(1920,1080);
            Surface surface = new Surface(texture);

            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

                builder.addTarget(surface);
                mCameraDevice.createCaptureSession(Arrays.asList(surface,mImageReader.getSurface()),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(),null,mBackgroundHandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }}, mBackgroundHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        public boolean isOpen() {
            if (mCameraDevice == null) {
                return false;
            } else {
                return true;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        public void openCamera() {
            try {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mCameraManager.openCamera(mCameraID,mCameraCallback,mBackgroundHandler);
                }
            } catch (CameraAccessException e) {
                Log.i(LOG_TAG,e.getMessage());
            }
        }

        public void closeCamera() {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    }

    private static class ImageSaver implements Runnable {
        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;

        ImageSaver(Image image, File file) {
            mImage = image;
            mFile = file;
        }

        @Override
        public void run() {
            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                if (null != output) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    void initializeData(){
        registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        registerReceiver(mReceiver,new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        String devProtocol = devicesList.get(0).getDevProtocol();
        findViewById(R.id.button_stop_bt).setEnabled(false);
        findViewById(R.id.button_stop_bt_right).setEnabled(false);
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

        getDevicesID = new ProtocolRepo(getApplicationContext(), devProtocol);
        ProtocolDBHelper protocolDBHelper = ProtocolDBHelper.getInstance(getApplicationContext());
        lengthMes = protocolDBHelper.getLength(devProtocol);
        message = new byte[lengthMes];
        countCommands = 0;

        findViewById(R.id.button_up_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_down_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_left_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_right_bt).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop_bt).setOnClickListener(this);

        findViewById(R.id.button_up_bt_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_down_bt_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_left_bt_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_right_bt_right).setOnTouchListener(touchListener);
        findViewById(R.id.button_stop_bt_right).setOnClickListener(this);

        //1
//        btnCamera = findViewById(R.id.btn_camera);
//        btnCamera.setOnClickListener(this);
//        mTextureView = findViewById(R.id.textureView);
//        mTextureView.setSurfaceTextureListener(this);

        SwitchMaterial hold_command = findViewById(R.id.switch_hold_command_mm_Bt);
        hold_command.setOnCheckedChangeListener(this);
        hold_command.setChecked(false);

        Arrays.fill(message, (byte) 0);
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
        for (DeviceItemType currentDevice : devicesList) {
            if (!currentDevice.isWiFiBtConnected()) {
                disconnectedDevicesList.add(currentDevice);
            }
        }
        for (DeviceItemType currentDevice : disconnectedDevicesList) {
            if (currentDevice.isWiFiBtConnected()) {
                devicesList.add(currentDevice);
            }
        }
        devicesList.removeIf(currentDevice -> !currentDevice.isWiFiBtConnected());
        disconnectedDevicesList.removeIf(DeviceItemType::isWiFiBtConnected);
    }

    public synchronized void addDisconnectedDevice() {
        ArrayList<DeviceItemType> current = disconnectedDevicesList;
        checkForActiveDevices();
        if((disconnectedDialog == null || !disconnectedDialog.isShowing())
                && ((isBtService && App.isBtEnabled()) ||
                (!isBtService && App.isWiFiEnabled()))
                && disconnectedDevicesList!=current){
            materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
            materialAlertDialogBuilder.setTitle(getString(R.string.error));
            if(disconnectedDevicesList.size()==1){
                materialAlertDialogBuilder.setMessage("Устройство " + devicesList.get(0).getDevName() + "отключилось. Продолжить работу?");
            } else {
                materialAlertDialogBuilder.setMessage("Некоторые устройства отключились. Продолжить работу?");
            }
            materialAlertDialogBuilder.setPositiveButton("Продолжить работу", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addDisconnectedDevice();
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
        } else if (((isBtService && !App.isBtEnabled()) || (!isBtService && !App.isWiFiEnabled()))
                && (networkDialog== null || !networkDialog.isShowing())){
            if(networkDialog != null && disconnectedDialog.isShowing())
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

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        checkForActiveDevices();

        startBackgroundThread();

        //arduino.BluetoothConnectionServiceVoid();     // соединяемся с bluetooth
        //TODO - вызывает вылет приложения
    }

    @Override
    protected void onPause() {

        if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
        if(myCameras[CAMERA2].isOpen()){myCameras[CAMERA2].closeCamera();}
        stopBackgroundThread();
        Log.d(LOG_TAG, "stopBackgroundThread");

        super.onPause();
        active = false;
        completeDevicesInfo();

        if (getDevicesID.getTag(res.getString(R.string.TAG_TURN_COM)))
            message[countCommands++] = getDevicesID.get("new_command");

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
            message[countCommands++] = getDevicesID.get("type_move");

        message[countCommands++] = getDevicesID.get("STOP");
        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
            Log.d(APP_LOG_TAG, "DeviceActivity в onPause");
            dataThreadForArduinoList.get(i).sendData(message, lengthMes);
        }

        for (int i = 0; i < dataThreadForArduinoList.size(); i++) {
            dataThreadForArduinoList.get(i).Disconnect();
        }
    }

    //1 фотосъёмка
//    public void capturePhoto() {
//        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_TAKE_PHOTO);
//        }
//    }

    //1 видеосъёмка
//    public void captureVideo() {
//        Intent intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_TAKE_VIDEO);
//        }
//    }

    @Override
    public void onClick(View v) {
        completeDevicesInfo();
        switch (v.getId()) {
            case (R.id.button_stop_bt):
                outputText.append("\n" + getResources().getString(R.string.send_command_stop));
                completeMessage("STOP");
                countCommands = 0;
                break;
            case (R.id.button_stop_bt_right):
                outputText.append("\n" + getResources().getString(R.string.send_command_stop));
                completeMessage("STOP");
                countCommands = 0;
                break;

//1
//            case R.id.btn_camera:
//                try {
//                    // Намерение для запуска камеры
//                    Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(captureIntent, CAMERA_REQUEST);
//                    String message = "Ваше устройство поддерживает съемку";
//                    Toast toast = Toast
//                            .makeText(this, message, Toast.LENGTH_SHORT);
//                    toast.show();
//                    Log.d("Camera", "Ваше устройство поддерживает съемку");
//                } catch (ActivityNotFoundException e) {
//                    // Выводим сообщение об ошибке
//                    String errorMessage = "Ваше устройство не поддерживает съемку";
//                    Toast toast = Toast
//                            .makeText(this, errorMessage, Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//                captureVideo();
//                Log.d("Camera", "Video is available");
//                break;
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
                    case (R.id.button_up_bt_right):
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
                    case (R.id.button_down_bt_right):
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
                    case (R.id.button_left_bt_right):
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
                    case (R.id.button_right_bt_right):
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
                        case (R.id.button_up_bt_right):
                            completeMessage("FORWARD_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_down_bt):
                            completeMessage("BACK_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_down_bt_right):
                            completeMessage("BACK_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_left_bt):
                            completeMessage("LEFT_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_left_bt_right):
                            completeMessage("LEFT_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_right_bt):
                            completeMessage("RIGHT_STOP");
                            countCommands = 0;
                            break;
                        case (R.id.button_right_bt_right):
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
        if (getDevicesID.getTag(res.getString(R.string.TAG_CLASS_FROM)))
            message[countCommands++] = getDevicesID.get("class_android");

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_FROM)))
            message[countCommands++] = getDevicesID.get("type_computer"); // класс и тип устройства отправки

        if (getDevicesID.getTag(res.getString(R.string.TAG_CLASS_TO)))
            message[countCommands++] = getDevicesID.get(devicesList.get(0).getDevClass());

        if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_TO)))
            message[countCommands++] = getDevicesID.get(devicesList.get(0).getDevType());// класс и тип устройства приема
    }

    public void completeMessage(String command) {

        Byte code = getDevicesID.get(command);
        if (code != null) {
            if (getDevicesID.getTag(res.getString(R.string.TAG_TURN_COM))) {
                message[countCommands++] = (prevCommand == code) ? getDevicesID.get("redo_command") : getDevicesID.get("new_command");
                prevCommand = code;
            }

            if (getDevicesID.getTag(res.getString(R.string.TAG_TYPE_COM)))
                message[countCommands++] = getDevicesID.get("type_move");
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
                    findViewById(R.id.button_stop_bt_right).setEnabled(true);
                } else {
                    outputText.append("\n" + getResources().getString(R.string.send_command_hold_disabled));
                    findViewById(R.id.button_stop_bt).setEnabled(false);
                    findViewById(R.id.button_stop_bt_right).setEnabled(false);
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

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

    }
}
