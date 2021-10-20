package ru.hse.control_system_v2.list_devices;

import static ru.hse.control_system_v2.Constants.APP_LOG_TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.ArrayList;

public class ItemsDiffUtilCallBack extends DiffUtil.Callback{
    ArrayList<ItemType> newList;
    ArrayList<ItemType> oldList;

    public ItemsDiffUtilCallBack(ArrayList<ItemType> newList, ArrayList<ItemType> oldList) {
        this.newList = newList;
        this.oldList = oldList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        // In the real world you need to compare something unique like id
        return oldList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return (newList.get(newItemPosition).getTextInfo().equals(oldList.get(oldItemPosition).getTextInfo()))
                && (newList.get(newItemPosition).getClass() == oldList.get(oldItemPosition).getClass());
    }

}
