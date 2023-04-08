package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.debug;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.general.WidgetChangeListener;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.BaseDetailSubscriberVH;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.SubscriberWidgetViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.utility.Utils;
import ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.joystick.JoystickEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * TODO: Description
 *
 * @author Nils Rottmann
 * @version 1.1.0
 * @created on 17.08.20
 * @updated on 17.09.20
 * @modified by Nils Rottmann
 * @updated on 17.03.21
 * @modified by Nico Studt
 */
public class DebugDetailVH extends SubscriberWidgetViewHolder implements TextView.OnEditorActionListener {

    protected EditText messageNumberEdittext;


    @Override
    protected void initView(View parentView) {
        messageNumberEdittext = parentView.findViewById(R.id.messageNumberEdittext);
        messageNumberEdittext.setOnEditorActionListener(this);
    }

    @Override
    protected void bindEntity(BaseEntity widget) {
        DebugEntity entity = (DebugEntity) widget;
        messageNumberEdittext.setText(String.valueOf(entity.numberMessages));

    }

    @Override
    protected void updateEntity(BaseEntity widget) {
        DebugEntity entity = (DebugEntity) widget;
        entity.numberMessages = Integer.parseInt(messageNumberEdittext.getText().toString());
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId){
            case EditorInfo.IME_ACTION_DONE:
            case EditorInfo.IME_ACTION_NEXT:
            case EditorInfo.IME_ACTION_PREVIOUS:
                Utils.hideSoftKeyboard(itemView);
                itemView.requestFocus();
                return true;
        }

        return false;
    }

    @Override
    public List<String> getTopicTypes() {
        return new ArrayList<>();
    }

}