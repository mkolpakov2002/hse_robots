package ru.hse.control_system_v2.ui.device_settings;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.hse.control_system_v2.App;
import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.AppDatabase;
import ru.hse.control_system_v2.data.DeviceItemType;
import ru.hse.control_system_v2.data.DeviceItemTypeDao;
import ru.hse.control_system_v2.ui.MainActivity;
import ru.hse.control_system_v2.ui.SpinnerArrayAdapter;
import ru.hse.control_system_v2.ui.TextChangedListener;

public class DeviceMenuFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String name, MAC, protocol, devClass, devType, devIp, devPort, imageType, devVideoCommand;
    private int id;
    private DeviceItemType currentDevice;
    private MainActivity ma;
    private AlertDialog alertDialog;
    TextInputEditText deviceNameView;
    TextInputEditText deviceMACView;
    TextInputEditText deviceVideoCommandView;
    TextInputLayout deviceDevVideoCommandLayout;
    MaterialAutoCompleteTextView deviceClassView;
    MaterialAutoCompleteTextView deviceTypeView;
    MaterialAutoCompleteTextView deviceProtoView;
    TextInputEditText deviceIpView;
    TextInputEditText devicePortView;
    private String mPreviousMac = null;
    private List<String> listClasses, listTypes;
    private ArrayList<String> data;
    MaterialButton connectButton;
    MaterialButton deleteButton;
    MaterialButton saveButton;
    Context fragmentContext;
    View view;
    ImageView deviceImage;
    TextInputLayout deviceTypeViewLayout;
    SpinnerArrayAdapter<String> adapterType;
    boolean isNew = true;
    ImageView btIcon;
    ImageView wifiIcon;

    @Override
    public void onAttach(@NonNull Context context) {
        fragmentContext = context;
        ma = ((MainActivity) fragmentContext);
        super.onAttach(context);
    }

    public DeviceMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Bundle b = getArguments();
        if(b!=null) {
            isNew = b.getBoolean("isNew");
            currentDevice = (DeviceItemType) b.getSerializable("device");
        }
        getDeviceInformation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_device_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        showDeviceInformation(view);
    }

    void getDeviceInformation() {
        id = currentDevice.getDevId();
        name = currentDevice.getName();
        MAC = currentDevice.getDeviceMAC();
        protocol = currentDevice.getDevProtocol();
        devClass = currentDevice.getDevClass();
        devType = currentDevice.getDevType();
        devIp = currentDevice.getDevIp();
        devPort = String.valueOf(currentDevice.getDevPort());
//        imageType = currentDevice.getImageType();
    }

    void showDeviceInformation(View view) {
        deviceImage = view.findViewById(R.id.icon_image_view_menu);
        saveButton = view.findViewById(R.id.device_save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDevice();
            }
        });
        deviceTypeViewLayout = view.findViewById(R.id.device_type_layout);
        deviceNameView = view.findViewById(R.id.device_name_edit);
        deviceNameView.setText(name);
        deviceNameView.addTextChangedListener(new TextChangedListener<>(deviceNameView) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                name = s.toString().trim();
                if (s.toString().trim().length() == 0) {
                    target.setError(getString(R.string.error_incorrect));
                }
                onRefresh();
            }
        });
        deviceVideoCommandView = view.findViewById(R.id.device_dev_video_command_edit);
        deviceVideoCommandView.setText(devVideoCommand);
        deviceVideoCommandView.addTextChangedListener(new TextChangedListener<>(deviceVideoCommandView) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                devVideoCommand = s.toString().trim();
                onRefresh();
            }
        });

        deviceDevVideoCommandLayout = view.findViewById(R.id.device_dev_video_command_layout);
        if(!isWiFiSupported()){
            deviceDevVideoCommandLayout.setEnabled(false);
        }
        deviceDevVideoCommandLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
            }
        });

        deviceMACView = view.findViewById(R.id.device_mac_edit);
        deviceMACView.setText(MAC);
        deviceMACView.addTextChangedListener(new TextWatcher() {
            //https://github.com/r-cohen/macaddress-edittext
            private void setMacEdit(String cleanMac, String formattedMac, int selectionStart, int lengthDiff) {
                deviceMACView.removeTextChangedListener(this);
                if (cleanMac.length() <= 12) {
                    deviceMACView.setText(formattedMac);
                    deviceMACView.setSelection(selectionStart + lengthDiff);
                    mPreviousMac = formattedMac;
                } else {
                    deviceMACView.setText(mPreviousMac);
                    deviceMACView.setSelection(mPreviousMac.length());
                }
                deviceMACView.addTextChangedListener(this);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!deviceMACView.getText().toString().equals(deviceMACView.getText().toString())) {
                    String upperText = deviceMACView.getText().toString().toUpperCase();
                    deviceMACView.setText(upperText);
                    deviceMACView.setSelection(deviceMACView.length()); //fix reverse texting
                }
                String enteredMac = deviceMACView.getText().toString().toUpperCase();
                String cleanMac = clearNonMacCharacters(enteredMac);
                String formattedMac = formatMacAddress(cleanMac);
                int selectionStart = deviceMACView.getSelectionStart();
                formattedMac = handleColonDeletion(enteredMac, formattedMac, selectionStart);
                int lengthDiff = formattedMac.length() - enteredMac.length();
                setMacEdit(cleanMac, formattedMac, selectionStart, lengthDiff);
                MAC = s.toString().trim();
                onRefresh();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        deviceClassView = view.findViewById(R.id.device_class_edit);
        deviceClassView.setText(devClass);
        SpinnerArrayAdapter<String> adapterClass = new SpinnerArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("class_android", "class_computer", "class_arduino", "no_class"));
        deviceClassView.setAdapter(adapterClass);
        deviceClassView.addTextChangedListener(new TextChangedListener<>(deviceClassView) {
            @Override
            public void onTextChanged(MaterialAutoCompleteTextView target, Editable s) {
                if(!devClass.equals(s.toString())){
                    devClass = s.toString();
                    onRefresh();
                }
            }
        });
        deviceTypeView = view.findViewById(R.id.device_type_edit);
        deviceTypeView.setText(devType);
        adapterType = new SpinnerArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList("type_sphere", "type_anthropomorphic", "type_cubbi", "type_computer", "no_type"));

        deviceTypeView.setAdapter(adapterType);
        deviceTypeView.addTextChangedListener(new TextChangedListener<>(deviceTypeView) {
            @Override
            public void onTextChanged(MaterialAutoCompleteTextView target, Editable s) {
                if(deviceTypeViewLayout.isEnabled() && !devType.equals(s.toString())){
                    devType = s.toString();
                    onRefresh();
                }
            }
        });
        deviceProtoView = view.findViewById(R.id.device_proto_edit);
        deviceProtoView.setText(protocol);
        //TODO
//        SpinnerArrayAdapter<String> adapterProto = new SpinnerArrayAdapter<String>(
//                fragmentContext, android.R.layout.simple_spinner_dropdown_item,
//                AppDatabase.Companion.getProtocolNames());
//        deviceProtoView.setAdapter(adapterProto);
        deviceProtoView.addTextChangedListener(new TextChangedListener<>(deviceProtoView) {
            @Override
            public void onTextChanged(MaterialAutoCompleteTextView target, Editable s) {
                protocol = s.toString().trim();
                onRefresh();
            }
        });
        deviceIpView = view.findViewById(R.id.device_ip_edit);
        deviceIpView.setText(devIp);
        deviceIpView.addTextChangedListener(new TextChangedListener<>(deviceIpView) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                devIp = s.toString().trim();
                onRefresh();
            }
        });
        devicePortView = view.findViewById(R.id.device_port_edit);
        devicePortView.setText(devPort);
        devicePortView.addTextChangedListener(new TextChangedListener<>(devicePortView) {
            @Override
            public void onTextChanged(TextInputEditText target, Editable s) {
                devPort = s.toString().trim();
                if(devPort.length()==0)
                    devPort="0";
                onRefresh();
            }
        });
        connectButton = view.findViewById(R.id.device_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ma.showBottomSheetToConnect();
            }
        });
        if(isNew)
            connectButton.setVisibility(View.GONE);
        deleteButton = view.findViewById(R.id.device_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppDatabase dbDevices = AppDatabase.Companion.getAppDataBase(requireContext());
                //TODO
                DeviceItemTypeDao devicesDao = dbDevices.deviceItemTypeDao();
                if (devicesDao != null) {
                    //TODO
                    //devicesDao.delete(id);
                }
                Navigation.findNavController(view).navigate(R.id.action_deviceMenuFragment_to_mainMenuFragment);
            }
        });
        if(isNew)
            deleteButton.setVisibility(View.GONE);
        btIcon = view.findViewById(R.id.device_menu_bt_icon);
        wifiIcon = view.findViewById(R.id.device_menu_wifi_icon);
        onRefresh();
    }

    void saveDevice() {
        if (deviceNameView.getText().toString().trim().length() == 0) {
            deviceNameView.setError(getString(R.string.error_incorrect));
        } else if (deviceMACView.getText().toString().trim().length() > 0 && !isBtSupported()) {
            deviceMACView.setError(getString(R.string.error_incorrect));
        } else if (deviceIpView.getText().toString().trim().length() > 0 && !isWiFiSupported()) {
            deviceIpView.setError(getString(R.string.error_incorrect));
        } else {
            String newName = deviceNameView.getText().toString();
            protocol = deviceProtoView.getText().toString();
            String classDevice = deviceClassView.getText().toString();
            String typeDevice;
            if (classDevice.equals("class_arduino"))
                typeDevice = deviceTypeView.getText().toString();
            else
                typeDevice = "no_type";
            MAC = deviceMACView.getText().toString().trim();
            devIp = deviceIpView.getText().toString().trim();
            devPort = devicePortView.getText().toString().trim();

            //TODO
//            AppDatabase dbDevices = App.getDatabase();
//            DeviceItemTypeDao devicesDao = dbDevices.getDeviceItemTypeDao();
            currentDevice.setName(newName);
            currentDevice.setDeviceMAC(MAC);
            currentDevice.setDevClass(classDevice);
            currentDevice.setDevType(typeDevice);
            currentDevice.setDevProtocol(protocol);
            currentDevice.setDevIp(devIp);
            //TODO
            //currentDevice.setDevVideoCommand(devVideoCommand);
            try {
                int i = Integer.parseInt(devPort);
                currentDevice.setDevPort(i);
            } catch (NumberFormatException e) {
                currentDevice.setDevPort(0);
            }
            currentDevice.setDevPort(Integer.parseInt(devPort));
            //TODO
//            devicesDao.insertAll(currentDevice);
            Navigation.findNavController(view).navigate(R.id.mainMenuFragment);
        }
    }

    private String handleColonDeletion(String enteredMac, String formattedMac, int selectionStart) {
        if (mPreviousMac != null && mPreviousMac.length() > 1) {
            int previousColonCount = colonCount(mPreviousMac);
            int currentColonCount = colonCount(enteredMac);

            if (currentColonCount < previousColonCount) {
                formattedMac = formattedMac.substring(0, selectionStart - 1) + formattedMac.substring(selectionStart);
                String cleanMac = clearNonMacCharacters(formattedMac);
                formattedMac = formatMacAddress(cleanMac);
            }
        }
        return formattedMac;
    }

    private static String formatMacAddress(String cleanMac) {
        int grouppedCharacters = 0;
        StringBuilder formattedMac = new StringBuilder();
        for (int i = 0; i < cleanMac.length(); ++i) {
            formattedMac.append(cleanMac.charAt(i));
            ++grouppedCharacters;
            if (grouppedCharacters == 2) {
                formattedMac.append(":");
                grouppedCharacters = 0;
            }
        }
        if (cleanMac.length() == 12) {
            formattedMac = new StringBuilder(formattedMac.substring(0, formattedMac.length() - 1));
        }
        return formattedMac.toString();
    }

    private static String clearNonMacCharacters(String mac) {
        return mac.replaceAll("[^A-Fa-f/d]", "");
    }

    private static int colonCount(String formattedMac) {
        return formattedMac.replaceAll("[^:]", "").length();
    }

    void setDeviceImage() {
        if (devClass.equals("class_arduino")) {
            imageType = devType;
        } else {
            imageType = devClass;
        }
        if (devClass.equals("class_arduino")) {
            switch (imageType) {
                case "type_computer":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.type_computer));
                    break;
                case "type_sphere":
                    //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                    break;
                case "type_anthropomorphic":
                    //mViewHolder.deviceImage.setImageResource(R.drawable.type_computer);
                    break;
                case "type_cubbi":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.type_cubbi));
                    break;
                case "no_type":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.type_no_type));
                    break;
            }
        } else {
            switch (imageType) {
                case "class_android":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.class_android));
                    break;
                case "no_class":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.type_no_type));
                    break;
                case "class_computer":
                    deviceImage.setImageDrawable(ContextCompat.getDrawable(fragmentContext, R.drawable.class_computer));
                    break;
            }
        }
    }

    void onRefresh() {
        if (deviceClassView.getText().toString().equals("class_arduino")) {
            deviceTypeViewLayout.setEnabled(true);
            adapterType = new SpinnerArrayAdapter<String>(
                    requireActivity(), android.R.layout.simple_spinner_dropdown_item,
                    Arrays.asList("type_sphere", "type_anthropomorphic", "type_cubbi", "type_computer", "no_type"));
            deviceTypeView.setAdapter(adapterType);
        } else {
            deviceTypeViewLayout.setEnabled(false);
            deviceTypeView.setText("no_type");
        }
        setDeviceImage();

        if(name.equals(currentDevice.getName()) &&
                MAC.equals(currentDevice.getDeviceMAC()) &&
                protocol.equals(currentDevice.getDevProtocol()) &&
                devClass.equals(currentDevice.getDevClass()) &&
                devType.equals(currentDevice.getDevType()) &&
                devIp.equals(currentDevice.getDevIp()) &&
                devPort.equals(String.valueOf(currentDevice.getDevPort()))
                //TODO
//                &&
//                imageType.equals(currentDevice.getImageType())
        )
        {
            if(currentDevice.isBtSupported() ||
                    currentDevice.isWiFiSupported()){
                connectButton.setEnabled(true);
            }
            if(!isNew){
                saveButton.setEnabled(false);
                deleteButton.setEnabled(true);
            }
        } else {
            saveButton.setEnabled(true);
            connectButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }

        if(isBtSupported())
            btIcon.setVisibility(View.VISIBLE);
        else btIcon.setVisibility(View.INVISIBLE);

        if(isWiFiSupported())
            wifiIcon.setVisibility(View.VISIBLE);
        else wifiIcon.setVisibility(View.INVISIBLE);

        deviceDevVideoCommandLayout.setEnabled(isWiFiSupported());


    }

    boolean isBtSupported() {
        return MAC != null && BluetoothAdapter.checkBluetoothAddress(MAC);
    }

    boolean isWiFiSupported() {
        return ((devIp != null) && Patterns.IP_ADDRESS.matcher(devIp).matches());
    }

}