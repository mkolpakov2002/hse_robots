package ru.hse.control_system_v2.ui.connection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel

class DeviceControlFragment(val device: ConnectionDeviceModel) : Fragment() {

    // A view pager to display different screens for controlling the device
    lateinit var viewPager: ViewPager2

    // A tab layout to indicate the current screen and allow switching between screens
    lateinit var tabLayout: TabLayout

    // A list of titles for the screens
    val titles = listOf("Joystick", "Package Data", "Video Stream")

//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout for this fragment
//        val view = inflater.inflate(R.layout.fragment_device_control, container, false)
//
//        // Initialize the view pager and its adapter
//        viewPager = view.findViewById(R.id.view_pager)
//        viewPager.adapter = ScreenSlidePagerAdapter(this, device)
//
//        // Initialize the tab layout and attach it to the view pager
//        tabLayout = view.findViewById(R.id.tab_layout)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            // Set the tab text from the titles list
//            tab.text = titles[position]
//        }.attach()
//
//        return view
//    }
}