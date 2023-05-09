package ru.hse.control_system_v2.ui.connection

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel

class DeviceControlPagerAdapter(
    activity: FragmentActivity,
    val connectionDeviceModels: ArrayList<ConnectionDeviceModel>
) : FragmentStateAdapter(activity) {

    // A list of device control fragments to display for each device
    val fragments = ArrayList<DeviceControlFragment>()

    init {
        // Create a device control fragment for each connection device model and add it to the fragments list
        connectionDeviceModels.forEach { device ->
            fragments.add(DeviceControlFragment(device))
        }
    }

    override fun getItemCount(): Int {
        // Return the size of the connection device models list
        return connectionDeviceModels.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return the fragment at the given position
        return fragments[position]
    }

    // A function to remove an item from the adapter and notify the change
    fun removeItem(position: Int) {
        // Remove the connection device model and the fragment at the given position
        connectionDeviceModels.removeAt(position)
        fragments.removeAt(position)
        // Notify the adapter that the data has changed
        notifyDataSetChanged()
    }
}