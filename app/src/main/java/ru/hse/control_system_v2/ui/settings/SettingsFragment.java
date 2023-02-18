package ru.hse.control_system_v2.ui.settings;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.AppConstants;
import ru.hse.control_system_v2.ui.SpinnerArrayAdapter;
import ru.hse.control_system_v2.ui.TextChangedListener;
import ru.hse.control_system_v2.ui.theming.ThemeUtils;
import ru.hse.control_system_v2.data.ProtocolDBHelper;
import ru.hse.control_system_v2.data.ProtocolRepo;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static ru.hse.control_system_v2.AppConstants.THEMES_LIST_ANDROID_S;

public class SettingsFragment extends Fragment {

    private ProtocolDBHelper dbHelper;
    private TextInputEditText editTextName, editTextLen, editTextCode;
    private Button buttonAdd;
    private Button buttonShowProtoMenu;
    private TextView textListProtocols;
    private final int REQUEST_CODE_OPEN = 20, PERMISSION_REQUEST_CODE = 123;
    private SQLiteDatabase database;
    private boolean isEditTextNameChanged, isEditTextLenChanged, isEditTextCodeChanged;
    private ScrollView menuProto;
    private Context fragmentContext;
    SpinnerArrayAdapter<String> themesAdapter;
    private MaterialButton openDataParamsDialog;

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentContext = context;
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
        dbHelper = new ProtocolDBHelper();

        openDataParamsDialog = view.findViewById(R.id.buttonOpenDataSettings);
        openDataParamsDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(requireParentFragment().requireView()).navigate(R.id.action_settingsFragment_to_dialogDataParams);
            }
        });

        buttonAdd = view.findViewById(R.id.button_add_protocol);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString();
                String slength = editTextLen.getText().toString();
                int length;
                String code = editTextCode.getText().toString();
                ProtocolRepo protocolRepo = new ProtocolRepo(fragmentContext, "");
                int result = protocolRepo.stringXMLparser(code);
                if (result > 0) {
                    Toast.makeText(fragmentContext, "Invalid XML code", Toast.LENGTH_LONG).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(fragmentContext, "Invalid name", Toast.LENGTH_LONG).show();
                } else if (slength.isEmpty()) {
                    Toast.makeText(fragmentContext, "Invalid length", Toast.LENGTH_LONG).show();
                } else {
                    length = Integer.parseInt(slength);
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ProtocolDBHelper.KEY_NAME, name);
                    contentValues.put(ProtocolDBHelper.KEY_LEN, length);
                    try {
                        contentValues.put(ProtocolDBHelper.KEY_CODE, saveToFile(name, code));
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
                }

            }
        });
        //TODO
        //buttonAdd.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightDark));
        buttonAdd.setEnabled(false);

        ArrayList<String> themes;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            themes = new ArrayList<String>(Arrays.asList(THEMES_LIST_ANDROID_S));
        } else {
            themes = new ArrayList<String>(Arrays.asList(AppConstants.THEMES_LIST));
        }
        themesAdapter = new SpinnerArrayAdapter<>(fragmentContext, android.R.layout.simple_spinner_item, themes);
        themesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        MaterialAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.theme_menu);
        autoCompleteTextView.setThreshold(themes.size());
        SharedPreferences sPref = PreferenceManager.getDefaultSharedPreferences(App.getContext());
        String sTheme = sPref.getString("theme", themes.get(0));
        autoCompleteTextView.setText(sTheme);
        autoCompleteTextView.setAdapter(themesAdapter);
        autoCompleteTextView.setOnItemClickListener((adapterView, view1, position, l) -> {
            if (position < themes.size() && position >= 0 && !ThemeUtils.getCurrentTheme().equals(autoCompleteTextView.getText().toString())) {
                ThemeUtils.switchTheme(themes.get(position));
                autoCompleteTextView.clearFocus();
            }
        });

        editTextName = view.findViewById(R.id.editTextProtocolName);
        editTextName.addTextChangedListener(new TextChangedListener<>(editTextName) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                isEditTextNameChanged = s.toString().trim().length() != 0;
                canShowSaveButton();
            }
        });

        editTextLen = view.findViewById(R.id.editTextLength);
        editTextLen.addTextChangedListener(new TextChangedListener<>(editTextLen) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                isEditTextLenChanged = s.toString().trim().length() != 0;
                canShowSaveButton();
            }
        });

        editTextCode = view.findViewById(R.id.editTextCode);
        editTextCode.addTextChangedListener(new TextChangedListener<>(editTextCode) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(fragmentContext);
                materialAlertDialogBuilder.setTitle(getResources().getString(R.string.settings_fragment_confirm))
                        .setMessage(getResources().getString(R.string.settings_fragment_delete_protocols))
                        .setIcon(R.drawable.ic_baseline_warning_24)
                        .setPositiveButton(getResources().getString(R.string.settings_fragment_button_ok), (dialog1, whichButton) -> {
                            ProtocolDBHelper dbHelper = new ProtocolDBHelper();
                            dbHelper.onUpgrade(dbHelper.getWritableDatabase(), 1, 1);
                            showProtocols();
                        })
                        .setNegativeButton(getResources().getString(R.string.settings_fragment_button_cancel), null)
                        .create();
                materialAlertDialogBuilder.show();

            }
        });

        Button buttonDeleteDevices = view.findViewById(R.id.button_delete_devices);
        buttonDeleteDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
//                AppDatabase dbDevices = App.getDatabase();
//                DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
//                devicesDao.deleteAll();
            }
        });

        buttonShowProtoMenu = view.findViewById(R.id.button_show_add_proto);
        buttonShowProtoMenu
                .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                        R.drawable.ic_baseline_keyboard_arrow_right_24, 0);
        buttonShowProtoMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuProto.getVisibility() == VISIBLE) {
                    menuProto.setVisibility(GONE);
                    buttonShowProtoMenu
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_baseline_keyboard_arrow_right_24, 0);
                    Log.d("Button", "hide menu");
                } else {
                    Log.d("Button", "show menu");
                    menuProto.setVisibility(VISIBLE);
                    buttonShowProtoMenu
                            .setCompoundDrawablesWithIntrinsicBounds(0, 0,
                                    R.drawable.ic_baseline_keyboard_arrow_down_24, 0);
                }
            }
        });

        Button buttonFile = view.findViewById(R.id.button_choose_file);
        buttonFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hasPermissions()) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    String[] mimetypes = {"text/xml", "text/plain"};
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, REQUEST_CODE_OPEN);
                } else {
                    requestPermissionWithRationale();
                }
            }
        });
        showProtocols();
    }

    void canShowSaveButton() {
        if (isEditTextNameChanged && isEditTextLenChanged && isEditTextCodeChanged) {
            buttonAdd.setEnabled(true);
            //TODO
            //buttonAdd.setBackgroundColor(fragmentContext.getColor(R.color.white));
        } else {
            buttonAdd.setEnabled(false);
            //buttonAdd.setBackgroundColor(fragmentContext.getColor(R.color.colorPrimaryLightDark));
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

    private boolean hasPermissions() {
        int res = 0;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        for (String perms : permissions) {
            res = requireActivity().checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            Snackbar.make(requireActivity().findViewById(R.id.activity_explain_perms), message, Snackbar.LENGTH_LONG)
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

        if (!allowed) {
            // we will give warning to user that they haven't granted permissions.
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(requireActivity(), "Storage Permissions denied.", Toast.LENGTH_SHORT).show();
            } else {
                showNoStoragePermissionSnackbar();
            }
        }

    }


    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(requireActivity().findViewById(R.id.activity_explain_perms), "Storage permission isn't granted", Snackbar.LENGTH_LONG)
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
                Uri.parse("package:" + requireActivity().getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    private void requestPerms() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
    }

    private String readTextFile(Uri uri) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(requireActivity().getContentResolver().openInputStream(uri)));
            String line = "";
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private String saveToFile(String name, String code) throws IOException {
        String fileName = name + ".xml";
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new
                File(requireActivity().getFilesDir() + File.separator + name + ".xml")));
        bufferedWriter.write(code);
        bufferedWriter.close();
        return fileName;
    }

    void showProtocols() {
        Cursor cursor = database.query(ProtocolDBHelper.TABLE_PROTOCOLS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            textListProtocols.setText("");
            textListProtocols.append(getResources().getString(R.string.list_of_available_protocols));
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
            textListProtocols.append(getResources().getString(R.string.no_available_protocols));

        cursor.close();
    }

}
