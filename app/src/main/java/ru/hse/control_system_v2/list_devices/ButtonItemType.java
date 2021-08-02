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

    Context c;
    public ButtonItemType(Context c){
        this.c = c;
    }
    public View.OnClickListener getOnClickListener() {

       return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment navHostFragment = (NavHostFragment)((MainActivity) c).getSupportFragmentManager().getPrimaryNavigationFragment();
                assert navHostFragment != null;
                FragmentManager fragmentManager = navHostFragment.getChildFragmentManager();
                Fragment current = fragmentManager.getPrimaryNavigationFragment();
                if(current instanceof MainMenuFragment){
                    MainMenuFragment mainMenuFragment = (MainMenuFragment) current;
                    mainMenuFragment.showAddDeviceBottomMenu();
                }

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
