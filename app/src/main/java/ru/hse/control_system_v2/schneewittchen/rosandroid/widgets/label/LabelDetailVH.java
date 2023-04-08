package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.label;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import ru.hse.control_system_v2.R;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.general.WidgetChangeListener;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.BaseDetailViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.DetailViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.SilentWidgetViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.details.WidgetViewHolder;
import ru.hse.control_system_v2.schneewittchen.rosandroid.utility.Utils;
import androidx.annotation.NonNull;

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
public class LabelDetailVH extends SilentWidgetViewHolder {

    private EditText labelTextText;
    private Spinner labelTextRotationSpinner;

    private ArrayAdapter<CharSequence> rotationAdapter;


    @Override
    public void initView(View view) {
        labelTextText = view.findViewById(R.id.labelText);
        labelTextRotationSpinner = view.findViewById(R.id.labelTextRotation);

        // Init spinner
        rotationAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.button_rotation, android.R.layout.simple_spinner_dropdown_item);

        labelTextRotationSpinner.setAdapter(rotationAdapter);
    }

    @Override
    protected void bindEntity(BaseEntity entity) {
        LabelEntity labelEntity = (LabelEntity) entity;
        int position = rotationAdapter.getPosition(Utils.numberToDegrees(labelEntity.rotation));

        labelTextText.setText(labelEntity.text);
        labelTextRotationSpinner.setSelection(position);
    }

    @Override
    protected void updateEntity(BaseEntity entity) {
        int rotation = Utils.degreesToNumber(labelTextRotationSpinner.getSelectedItem().toString());

        ((LabelEntity) entity).text = labelTextText.getText().toString();
        ((LabelEntity) entity).rotation = rotation;
    }


}
