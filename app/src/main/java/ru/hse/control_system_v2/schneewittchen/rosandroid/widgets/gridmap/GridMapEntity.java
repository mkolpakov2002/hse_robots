package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.gridmap;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.SubscriberLayerEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;

import nav_msgs.OccupancyGrid;


/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 08.03.21
 */
public class GridMapEntity extends SubscriberLayerEntity {
    
    public GridMapEntity() {
        this.topic = new Topic("/move_base/local_costmap/costmap", OccupancyGrid._TYPE);
    }
    
}
