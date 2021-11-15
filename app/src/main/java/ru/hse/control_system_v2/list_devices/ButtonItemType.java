package ru.hse.control_system_v2.list_devices;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import ru.hse.control_system_v2.MainActivity;
import ru.hse.control_system_v2.MainMenuFragment;
import ru.hse.control_system_v2.R;

public class ButtonItemType implements ItemType {

    MainActivity ma;
    String textInfo;
    final String IMAGE_TYPE = "button_type";
    MainMenuFragment mainMenuFragment;

    public ButtonItemType(MainActivity ma){
        this.ma = ma;
        this.textInfo = ma.getResources().getString(R.string.button_add_device);
        NavHostFragment navHostFragment = (NavHostFragment)(ma).getSupportFragmentManager().getPrimaryNavigationFragment();

        FragmentManager fragmentManager = null;
        if (navHostFragment != null) {
            fragmentManager = navHostFragment.getChildFragmentManager();
        }

        Fragment current = null;
        if (fragmentManager != null) {
            current = fragmentManager.getPrimaryNavigationFragment();
        }
        if(current instanceof MainMenuFragment){
            mainMenuFragment = (MainMenuFragment) current;
        }
    }
    public View.OnClickListener getOnClickListener() {

       return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainMenuFragment.showBottomSheet();
            }
        };
    }

    @Override
    public int getDevId() {
        return -1;
    }

    @Override
    public int getItemViewType() {
        return ItemType.BUTTON_ITEM_TYPE;
    }

    @Override
    public void setIsSelectedOnScreen(boolean isSelectedOnScreen) {
        //button can not be selected
    }

    @Override
    public boolean getIsSelectedOnScreen() {
        return false;
    }

    @Override
    public String getImageType() {
        return IMAGE_TYPE;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder) {
        ViewHolderFactory.ButtonViewHolder buttonViewHolder = (ViewHolderFactory.ButtonViewHolder) viewHolder;
        buttonViewHolder.buttonLayout.setOnClickListener(getOnClickListener());
        buttonViewHolder.buttonTextInfo.setText(textInfo);
    }

    @Override
    public String getTextInfo() {
        return textInfo;
    }
}
