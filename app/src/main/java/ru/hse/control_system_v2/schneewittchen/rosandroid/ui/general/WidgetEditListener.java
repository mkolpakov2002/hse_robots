package ru.hse.control_system_v2.schneewittchen.rosandroid.ui.general;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;


/**
 * TODO: Description
 *
 * @author Sarthak Mittal
 * @version 1.0.1
 * @created on 01.07.21
 */
public interface WidgetEditListener {

    void onWidgetEdited(BaseEntity widgetEntity, boolean updateConfig);
}
