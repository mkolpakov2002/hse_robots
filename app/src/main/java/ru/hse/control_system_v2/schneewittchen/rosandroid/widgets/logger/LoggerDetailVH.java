package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.logger;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.SubscriberWidgetViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.utility.Utils;

import java.util.Collections;
import java.util.List;


/**
 * TODO: Description
 *
 * @author Dragos Circa
 * @version 1.0.0
 * @created on 02.11.2020
 * @updated on 18.11.2020
 * @modified by Nils Rottmann
 * @updated on 20.03.2021
 * @modified by Nico Studt
 */

public class LoggerDetailVH extends SubscriberWidgetViewHolder {

    private Spinner labelTextRotationSpinner;
    private ArrayAdapter<CharSequence> rotationAdapter;


    @Override
    public void initView(View view) {
        labelTextRotationSpinner = view.findViewById(R.id.loggerTextRotation);

        rotationAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.button_rotation, android.R.layout.simple_spinner_dropdown_item);
        labelTextRotationSpinner.setAdapter(rotationAdapter);
    }

    @Override
    protected void bindEntity(BaseEntity entity) {
        LoggerEntity loggerEntity = (LoggerEntity) entity;
        String degrees = Utils.numberToDegrees(loggerEntity.rotation);

        labelTextRotationSpinner.setSelection(rotationAdapter.getPosition(degrees));
    }

    @Override
    protected void updateEntity(BaseEntity entity) {
        LoggerEntity loggerEntity = (LoggerEntity) entity;
        String degrees = labelTextRotationSpinner.getSelectedItem().toString();

        loggerEntity.rotation = Utils.degreesToNumber(degrees);
    }

    @Override
    public List<String> getTopicTypes() {
        return Collections.singletonList(std_msgs.String._TYPE);
    }
}
