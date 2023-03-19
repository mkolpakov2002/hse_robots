package ru.hse.control_system_v2.ui.device_settings;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel;

public class NewBtDevicesAdapter extends RecyclerView.Adapter<NewBtDevicesAdapter.DevicesAdapterVh> {

    private final ArrayList<BluetoothDevice> devicePrototypeList;

    private final OnDeviceClicked onDeviceClicked;

    public NewBtDevicesAdapter(ArrayList<BluetoothDevice> devicePrototypeList, OnDeviceClicked onDeviceClicked) {
        this.devicePrototypeList = devicePrototypeList;
        this.onDeviceClicked = onDeviceClicked;
    }

    @NonNull
    @Override
    public NewBtDevicesAdapter.DevicesAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        return new DevicesAdapterVh(
                LayoutInflater.from(context).inflate(R.layout.item_add_bd_device,
                        parent,
                        false));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull NewBtDevicesAdapter.DevicesAdapterVh holder, int position) {

        BluetoothDevice userModel = devicePrototypeList.get(position);

        holder.deviceNameTextView.setText(userModel.getName());
        holder.deviceAddressTextView.setText(userModel.getAddress());

    }

    @Override
    public int getItemCount() {
        return devicePrototypeList.size();
    }


    public interface OnDeviceClicked {

        void selectedDevice(DeviceModel devicePrototype);

    }

    public class DevicesAdapterVh extends RecyclerView.ViewHolder {

        TextView deviceNameTextView;
        TextView deviceAddressTextView;
        ImageView imIcon;

        @SuppressLint("MissingPermission")
        public DevicesAdapterVh(@NonNull View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.deviceName);
            deviceAddressTextView = itemView.findViewById(R.id.deviceAddress);
            imIcon = itemView.findViewById(R.id.imageView);
            itemView.setOnClickListener(view -> {
                DeviceModel selected = new DeviceModel();
                selected.setName(
                        devicePrototypeList.get(getBindingAdapterPosition()).getName());
                selected.setBluetoothAddress(
                        devicePrototypeList.get(getBindingAdapterPosition()).getAddress());
                onDeviceClicked.selectedDevice(selected);
            });
        }
    }
}
