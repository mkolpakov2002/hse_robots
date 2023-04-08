package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.gps;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.SubscriberWidgetEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

import sensor_msgs.NavSatFix;


/**
 * TODO: Description
 *
 * @author Nils Rottmann
 * @version 1.0.0
 * @created on 05.05.20
 * @updated on 27.10.2020
 * @modified by Nico Studt
 */
public class GpsEntity extends SubscriberWidgetEntity {

    public GpsEntity() {
        this.width = 8;
        this.height = 8;
        this.topic = new Topic("gps", NavSatFix._TYPE);
    }
}

