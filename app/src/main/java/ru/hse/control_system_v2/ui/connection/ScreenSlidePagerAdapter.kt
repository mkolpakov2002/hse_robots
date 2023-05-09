package ru.hse.control_system_v2.ui.connection

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel

class ScreenSlidePagerAdapter(
    fragment: Fragment,
    val device: ConnectionDeviceModel
) : FragmentStateAdapter(fragment) {

    // A list of fragments to display as screens for controlling the device
    val fragments = listOf(
        VideoFragment(),
//        PackageDataFragment(device),
//        VideoStreamFragment(device)
    )

    override fun getItemCount(): Int {
        // Return the size of the fragments list
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return the fragment at the given position
        return fragments[position]
    }
}