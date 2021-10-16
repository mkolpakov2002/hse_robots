package ru.hse.control_system_v2.list_devices;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemType {
    int BUTTON_ITEM_TYPE = 0;
    int DEVICE_ITEM_TYPE = 1;
    int getItemViewType();
    void onBindViewHolder(RecyclerView.ViewHolder viewHolder);
}
