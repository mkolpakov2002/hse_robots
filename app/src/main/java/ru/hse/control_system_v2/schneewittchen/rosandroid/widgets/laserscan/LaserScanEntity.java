package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.laserscan;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.SubscriberLayerEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.opengl.visualisation.ROSColor;

import sensor_msgs.LaserScan;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 14.05.21
 */
public class LaserScanEntity extends SubscriberLayerEntity {

    public int pointsColor;
    public int areaColor;
    public int pointSize;
    public boolean showFreeSpace;


    public LaserScanEntity() {
        this.topic = new Topic("/scan", LaserScan._TYPE);
        this.pointsColor = ROSColor.fromHexAndAlpha("377dfa", 0.6f).toInt();
        this.areaColor = ROSColor.fromHexAndAlpha("377dfa", 0.2f).toInt();
        this.pointSize = 10;
        this.showFreeSpace = true;
    }
}
