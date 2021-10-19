package ru.hse.control_system_v2.list_devices;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.MainMenuFragment;

public class ButtonItemType implements ItemType {

    MainActivity ma;
    public ButtonItemType(MainActivity ma){
        this.ma = ma;
    }
    public View.OnClickListener getOnClickListener() {

       return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ma.showBottomSheet();
            }
        };
    }

    @Override
    public int getItemViewType() {
        return ItemType.BUTTON_ITEM_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ButtonViewHolder buttonViewHolder = (ViewHolderFactory.ButtonViewHolder) viewHolder;
        buttonViewHolder.buttonLayout.setOnClickListener(getOnClickListener());
    }
}
