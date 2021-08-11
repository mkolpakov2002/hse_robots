package ru.hse.control_system_v2.list_devices;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class DeviceItemType implements ItemType{
    private final String name;
    private final String deviceMAC;
    private final String devClass;
    private final String devType;
    private final String protocol;
    int id;

    public DeviceItemType(int id, String name, String deviceMAC, String protocol, String devClass, String devType) {
        this.name = name;
        this.deviceMAC = deviceMAC;
        this.id = id;
        this.protocol = protocol;
        this.devClass = devClass;
        this.devType = devType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceItemType item = (DeviceItemType) o;
        return name.equals(item.name) &&
                deviceMAC.equals(item.deviceMAC);
    }

    public String getMAC() {
        return deviceMAC;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDevClass() { return devClass; }

    public String getDevType() { return devType; }

    public String getProtocol() { return protocol; }

    @Override
    public int getItemViewType() {
        return ItemType.DEVICE_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ListDevicesHolder mViewHolder = (ViewHolderFactory.ListDevicesHolder) viewHolder;
        mViewHolder.mName.setText(name);
        mViewHolder.checkMark.setVisibility(View.GONE);
        mViewHolder.deviceImage.setVisibility(View.VISIBLE);

    }
}
