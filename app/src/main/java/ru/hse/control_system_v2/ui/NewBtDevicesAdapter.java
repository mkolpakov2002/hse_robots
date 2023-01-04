package ru.hse.control_system_v2.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.data.NewBtDevice;

public class NewBtDevicesAdapter extends RecyclerView.Adapter<NewBtDevicesAdapter.DevicesAdapterVh> {

    private final List<NewBtDevice> newBtDeviceList;

    private final SelectedDevice selectedDevice;

    public NewBtDevicesAdapter(List<NewBtDevice> newBtDeviceList, SelectedDevice selectedDevice) {
        this.newBtDeviceList = newBtDeviceList;

        this.selectedDevice = selectedDevice;
    }

    @NonNull
    @Override
    public NewBtDevicesAdapter.DevicesAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new DevicesAdapterVh(LayoutInflater.from(context).inflate(R.layout.item_add_bd_device, null));
    }

    @Override
    public void onBindViewHolder(@NonNull NewBtDevicesAdapter.DevicesAdapterVh holder, int position) {

        NewBtDevice userModel = newBtDeviceList.get(position);

        String devicename = userModel.getDeviceName();

        holder.tvDevicename.setText(devicename);

    }

    @Override
    public int getItemCount() {
        return newBtDeviceList.size();
    }


    public interface SelectedDevice {

        void selectedDevice(NewBtDevice newBtDevice);

    }

    public class DevicesAdapterVh extends RecyclerView.ViewHolder {

        TextView tvDevicename;
        ImageView imIcon;

        public DevicesAdapterVh(@NonNull View itemView) {
            super(itemView);
            tvDevicename = itemView.findViewById(R.id.devicename);
            imIcon = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(view -> selectedDevice.selectedDevice(newBtDeviceList.get(getAdapterPosition())));
        }
    }
}
