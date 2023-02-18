package ru.hse.control_system_v2.data

import androidx.room.PrimaryKey

abstract class ItemType
    (open var devId: Int,
     open var name: String?)