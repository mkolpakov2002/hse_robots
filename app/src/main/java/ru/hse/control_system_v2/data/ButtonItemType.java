package ru.hse.control_system_v2.data;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.MainActivity;

public class ButtonItemType implements ItemType {

    String textInfo;
    final String IMAGE_TYPE = "button_type";

    public ButtonItemType(MainActivity ma) {
        this.textInfo = ma.getResources().getString(R.string.button_add_device);
    }

    @Override
    public int getDevId() {
        return -1;
    }

    @Override
    public String getImageType() {
        return IMAGE_TYPE;
    }

    @Override
    public String getTextInfo() {
        return textInfo;
    }
}
