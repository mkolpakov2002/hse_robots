package ru.hse.control_system_v2.schneewittchen.rosandroid.ui.views.widgets;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.node.BaseData;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.general.DataListener;

/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 10.03.21
 */
public interface IPublisherView {

    void publishViewData(BaseData data);
    void setDataListener(DataListener listener);
}
