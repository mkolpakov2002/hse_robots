package ru.hse.control_system_v2.data.classes.device.model

/**
 * логика для управления согласно протоколу по USB
 */
interface USBConnectable {
    var manufacture: String
    var model: String
}