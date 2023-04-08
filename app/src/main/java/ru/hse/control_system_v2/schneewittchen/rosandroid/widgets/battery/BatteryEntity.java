package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.battery;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 13.05.2021
 */
public class BatteryEntity extends SubscriberWidgetEntity {

    public boolean displayVoltage;


    public BatteryEntity() {
        this.width = 1;
        this.height = 2;
        this.topic = new Topic("battery", sensor_msgs.BatteryState._TYPE);
        this.displayVoltage = false;
    }

}
