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
import ru.hse.control_system_v2.data.DeviceModel;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.DevicesAdapterVh> {

    private final List<DeviceModel> deviceModelList;

    private final SelectedDevice selectedDevice;

    public DevicesAdapter(List<DeviceModel> deviceModelList, SelectedDevice selectedDevice) {
        this.deviceModelList = deviceModelList;

        this.selectedDevice = selectedDevice;
    }

    @NonNull
    @Override
    public DevicesAdapter.DevicesAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        return new DevicesAdapterVh(LayoutInflater.from(context).inflate(R.layout.item_add_bd_device, null));
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesAdapter.DevicesAdapterVh holder, int position) {

        DeviceModel userModel = deviceModelList.get(position);

        String devicename = userModel.getDeviceName();

        holder.tvDevicename.setText(devicename);

    }

    @Override
    public int getItemCount() {
        return deviceModelList.size();
    }


    public interface SelectedDevice {

        void selectedDevice(DeviceModel deviceModel);

    }

    public class DevicesAdapterVh extends RecyclerView.ViewHolder {

        TextView tvDevicename;
        ImageView imIcon;

        public DevicesAdapterVh(@NonNull View itemView) {
            super(itemView);
            tvDevicename = itemView.findViewById(R.id.devicename);
            imIcon = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(view -> selectedDevice.selectedDevice(deviceModelList.get(getAdapterPosition())));
        }
    }
}
