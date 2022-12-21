package ru.hse.control_system_v2.data;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.ui.MainActivity;
import ru.hse.control_system_v2.ui.MainMenuFragment;
import ru.hse.control_system_v2.ui.ViewHolderFactory;

public class ButtonItemType implements ItemType {

    MainActivity ma;
    String textInfo;
    final String IMAGE_TYPE = "button_type";
    MainMenuFragment mainMenuFragment;

    public ButtonItemType(MainActivity ma) {
        this.ma = ma;
        this.textInfo = ma.getResources().getString(R.string.button_add_device);
    }

    public View.OnClickListener getOnClickListener() {

        return v -> {
            mainMenuFragment = ma.getMainMenuFragment();
            if (mainMenuFragment != null) {
                mainMenuFragment.showBottomSheetToAdd();
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
