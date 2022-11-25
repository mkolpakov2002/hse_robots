package ru.hse.control_system_v2.data;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemType {
    int BUTTON_ITEM_TYPE = 0;
    int DEVICE_ITEM_TYPE = 1;

    int getDevId();

    int getItemViewType();

    void setIsSelectedOnScreen(boolean isSelectedOnScreen);

    boolean getIsSelectedOnScreen();

    String getImageType();

    void onBindViewHolder(RecyclerView.ViewHolder viewHolder);

    String getTextInfo();
}
