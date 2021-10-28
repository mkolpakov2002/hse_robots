package ru.hse.control_system_v2;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.hse.control_system_v2.dbprotocol.ProtocolDBHelper;
import ru.hse.control_system_v2.dbprotocol.ProtocolRepo;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private ProtocolDBHelper dbHelper;
    private EditText editTextName, editTextLen, editTextCode;
    private Button buttonAdd;
    private Button buttonShowProtoMenu;
    private TextView textListProtocols;
    private final int REQUEST_CODE_OPEN = 20, PERMISSION_REQUEST_CODE = 123;
    private SQLiteDatabase database;
    private boolean isEditTextNameChanged, isEditTextLenChanged, isEditTextCodeChanged;
    private ScrollView menuProto;
    private BluetoothAdapter btAdapter;
    private Context fragmentContext;
    private MainActivity ma;

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentContext=context;
        ma = ((MainActivity) fragmentContext);
        super.onAttach(context);
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dbHelper = new ProtocolDBHelper(fragmentContext);

        fragmentContext.registerReceiver(BluetoothStateChanged, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        buttonAdd = view.findViewById(R.id.button_add_protocol);
        buttonAdd.setOnClickListener(this);
        buttonAdd.setBackgroundColor(getResources().getColor(R.color.foregroundColor));
        buttonAdd.setEnabled(false);

        editTextName = view.findViewById(R.id.editTextProtocolName);
        editTextName.addTextChangedListener(new TextChangedListener<EditText>(editTextName) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                isEditTextNameChanged = s.toString().trim().length() != 0;
                canShowSaveButton();
            }
        });
        editTextLen = view.findViewById(R.id.editTextLength);
        editTextLen.addTextChangedListener(new TextChangedListener<EditText>(editTextLen) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                isEditTextLenChanged = s.toString().trim().length() != 0;
                canShowSaveButton();
            }
        });
        editTextCode = view.findViewById(R.id.editTextCode);
        editTextCode.addTextChangedListener(new TextChangedListener<EditText>(editTextCode) {
            @Override
            public void onTextChanged(EditText target, Editable s) {
                isEditTextCodeChanged = s.toString().trim().length() != 0;
                canShowSaveButton();
            }
        });

        editTextCode.setMovementMethod(new ScrollingMovementMethod());

        menuProto = view.findViewById(R.id.add_proto_scroll);
        menuProto.setVisibility(GONE);

        textListProtocols = view.findViewById(R.id.text_protocols);
        textListProtocols.setMovementMethod(new ScrollingMovementMethod());

        database = dbHelper.getWritableDatabase();

        Button buttonCancel = view.findViewById(R.id.button_delete_protos);
        buttonCancel.setOnClickListener(this);

        Button buttonDeleteDevices = view.findViewById(R.id.button_delete_devices);
        buttonDeleteDevices.setOnClickListener(this);

        buttonShowProtoMenu = view.findViewById(R.id.button_show_add_proto);
        buttonShowProtoMenu.setOnClickListener(this);

        Button buttonFile = view.findViewById(R.id.button_choose_file);
        buttonFile.setOnClickListener(this);
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        showProtocols();
    }


    void canShowSaveButton(){
        if (isEditTextNameChanged && isEditTextLenChanged && isEditTextCodeChanged){
            buttonAdd.setEnabled(true);
            buttonAdd.setBackgroundColor(fragmentContext.getColor(R.color.white));
        } else {
            buttonAdd.setEnabled(false);
            buttonAdd.setBackgroundColor(fragmentContext.getColor(R.color.foregroundColor));
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_delete_devices:
                AppDataBase dbDevices = App.getDatabase();
                DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
                devicesDao.deleteAll();
            case R.id.button_add_protocol:
                String name = editTextName.getText().toString();
                String slength = editTextLen.getText().toString();
                int length;
                String code = editTextCode.getText().toString();
                ProtocolRepo protocolRepo = new ProtocolRepo(fragmentContext, "");
                int result = protocolRepo.stringXMLparser(code);
                if (result > 0) {
                    Toast.makeText(fragmentContext, "Invalid XML code", Toast.LENGTH_LONG).show();
                    break;
                }
                if (name.isEmpty()) {
                    Toast.makeText(fragmentContext, "Invalid name", Toast.LENGTH_LONG).show();
                    break;
                }
                if (slength.isEmpty()) {
                    Toast.makeText(fragmentContext, "Invalid length", Toast.LENGTH_LONG).show();
                    break;
                }
                else
                    length = Integer.parseInt(slength);
                ContentValues contentValues = new ContentValues();
                contentValues.put(ProtocolDBHelper.KEY_NAME, name);
                contentValues.put(ProtocolDBHelper.KEY_LEN, length);
                try {
                    contentValues.put(ProtocolDBHelper.KEY_CODE, saveToFile(name,code));
                } catch (IOException e) {
                    Toast.makeText(fragmentContext, "Error saving: try again", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                if (dbHelper.insert(contentValues) == 0)
                    Toast.makeText(fragmentContext, "Protocol has already been registered", Toast.LENGTH_LONG).show();
                else {
                    editTextName.setText("");
                    editTextLen.setText("");
                    editTextCode.setText("");
                    Toast.makeText(fragmentContext, "Accepted", Toast.LENGTH_LONG).show();
                    showProtocols();
                }

                break;

            case R.id.button_delete_protos:
                AlertDialog dialog =new AlertDialog.Builder(fragmentContext)
                        .setTitle("Подтверждение")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage("Вы действительно хотите удалить все кастомные протоколы?")
                        .setPositiveButton("OK", (dialog1, whichButton) -> {
                            ProtocolDBHelper dbHelper = new ProtocolDBHelper(fragmentContext);
                            dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1,1);
                            showProtocols();
                        })
                        .setNegativeButton("Отмена", null)
                        .create();
                dialog.show();
                break;

            case R.id.button_choose_file:
                if (hasPermissions()){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    String[] mimetypes = {"text/xml", "text/plain"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, REQUEST_CODE_OPEN);
                }
                else {
                    requestPermissionWithRationale();
                }

                break;

            case R.id.button_show_add_proto:
                if (menuProto.getVisibility() == VISIBLE) {
                    menuProto.setVisibility(GONE);
                    buttonShowProtoMenu
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_baseline_keyboard_arrow_right_24, 0);
                } else if (menuProto.getVisibility() == GONE){
                    menuProto.setVisibility(VISIBLE);
                    buttonShowProtoMenu
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String fileContent = readTextFile(uri);
                editTextCode.setText(fileContent);
            }
        }
    }

    private boolean hasPermissions(){
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions){
            res = ma.checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ma,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(ma.findViewById(R.id.activity_explain_perms), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", v -> requestPerms())
                    .show();
        } else {
            requestPerms();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allowed = true;

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults) {
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed) {

        } else {
            // we will give warning to user that they haven't granted permissions.
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(ma, "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
            } else {
                showNoStoragePermissionSnackbar();
            }
        }

    }



    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(ma.findViewById(R.id.activity_explain_perms), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(fragmentContext,
                                "Open Permissions and grant the Storage permission",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + ma.getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions,PERMISSION_REQUEST_CODE);
    }

    private String readTextFile(Uri uri)
    {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try
        {
            reader = new BufferedReader(new InputStreamReader(ma.getContentResolver().openInputStream(uri)));
            String line = "";
            while ((line = reader.readLine()) != null)
            {
                builder.append(line).append("\n");
            }
            reader.close();
        }
        catch (IOException e) {e.printStackTrace();}
        return builder.toString();
    }

    private String saveToFile(String name, String code) throws IOException {
        String fileName = name + ".xml";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new
                File(ma.getFilesDir() + File.separator + name + ".xml")));
        bufferedWriter.write(code);
        bufferedWriter.close();
        return fileName;
    }

    void showProtocols(){
        Cursor cursor = database.query(ProtocolDBHelper.TABLE_PROTOCOLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            textListProtocols.setText("");
            textListProtocols.append("Список доступных протоколов");
            int idIndex = cursor.getColumnIndex(ProtocolDBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(ProtocolDBHelper.KEY_NAME);
            int lenIndex = cursor.getColumnIndex(ProtocolDBHelper.KEY_LEN);
            int codeIndex = cursor.getColumnIndex(ProtocolDBHelper.KEY_CODE);
            do {
                textListProtocols.append("\n" + "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", length = " + cursor.getString(lenIndex) +
                        ", code = " + cursor.getString(codeIndex));
            } while (cursor.moveToNext());
        } else
            textListProtocols.append("Нет доступных протоколов");

        cursor.close();
    }

    //True, если Bluetooth включён
    public boolean btIsEnabledFlagVoid(){
        return btAdapter.isEnabled();
    }

    //выполняемый код при изменении состояния bluetooth
    private final BroadcastReceiver BluetoothStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(btIsEnabledFlagVoid()){
                // Bluetooth включён, надо скрыть кнопку включения Bluetooth
                ma.hideFabToEnBt();
            } else {
                // Bluetooth выключён, надо показать кнопку включения Bluetooth
                ma.showFabToEnBt();
            }
        }
    };

}
