package ru.hse.control_system_v2.data.classes

import ru.hse.control_system_v2.data.classes.device.model.ItemType

/**
 * Ui класс элемента кнопки для добавления устройства на главном экране
 */
class ButtonItemType(
    override var id: Int,
    override var name: String
): ItemType