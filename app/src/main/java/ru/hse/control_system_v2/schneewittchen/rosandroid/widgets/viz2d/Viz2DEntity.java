package ru.hse.control_system_v2.schneewittchen.rosandroid.widgets.viz2d;


import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.GroupEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.ui.general.Position;

/**
 * TODO: Description
 *
 * @author Nico Studt
 * @version 1.0.0
 * @created on 08.03.21
 */
public class Viz2DEntity extends GroupEntity {

    public String frame;


    public Viz2DEntity() {
        this.width = 8;
        this.height = 8;
        this.frame = "map";
    }

}
