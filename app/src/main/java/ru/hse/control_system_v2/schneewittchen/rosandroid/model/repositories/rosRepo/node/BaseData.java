package ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.node;


import org.ros.internal.message.Message;
import org.ros.node.topic.Publisher;

import ru.hse.control_system_v2.schneewittchen.rosandroid.model.entities.widgets.BaseEntity;
import ru.hse.control_system_v2.schneewittchen.rosandroid.model.repositories.rosRepo.message.Topic;


public abstract class BaseData {

    protected Topic topic;


    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Topic getTopic() {
        return this.topic;
    }

    public Message toRosMessage(Publisher<Message> publisher, BaseEntity widget){
        return null;
    }
}
