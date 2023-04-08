package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.touchgoal;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.PublisherLayerEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

import geometry_msgs.PoseStamped;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 26.05.2021
 */
public class TouchGoalEntity extends PublisherLayerEntity {

    public TouchGoalEntity() {
        this.topic = new Topic("/goal", PoseStamped._TYPE);
        this.immediatePublish = true;
    }
}
