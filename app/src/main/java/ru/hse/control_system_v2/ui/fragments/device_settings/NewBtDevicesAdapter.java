package ru.hse.control_system_v2.ui.fragments.device_settings;

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
import ru.hse.control_system_v2.model.entities.Device;

public class NewBtDevicesAdapter extends RecyclerView.Adapter<NewBtDevicesAdapter.DevicesAdapterVh> {

    private ArrayList<BluetoothDevice> devicePrototypeList;

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
                LayoutInflater.from(context).inflate(R.layout.item_bt_paired_device,
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

    public void setDevicePrototypeList(ArrayList<BluetoothDevice> devicePrototypeList){
        this.devicePrototypeList = devicePrototypeList;
        notifyDataSetChanged();
    }


    public interface OnDeviceClicked {

        void selectedDevice(Device devicePrototype);

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
                Device selected = new Device();
                selected.setName(
                        devicePrototypeList.get(getBindingAdapterPosition()).getName());
                selected.setBluetoothAddress(
                        devicePrototypeList.get(getBindingAdapterPosition()).getAddress());
                onDeviceClicked.selectedDevice(selected);
            });
        }
    }
}
