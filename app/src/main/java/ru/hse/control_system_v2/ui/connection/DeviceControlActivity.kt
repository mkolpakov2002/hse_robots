package ru.hse.control_system_v2.ui.connection

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.connection.data.classes.ConnectionDeviceModel
import java.io.Serializable

class DeviceControlActivity : AppCompatActivity() {

    // A list of connection device models to store the selected devices and their protocols
    lateinit var connectionDeviceModels: ArrayList<ConnectionDeviceModel>

    // A view pager to display different fragments for controlling each device
    lateinit var viewPager: ViewPager2

    // A tab layout to indicate the current fragment and allow switching between fragments
    lateinit var tabLayout: TabLayout

    // A button to go to the previous fragment
    lateinit var prevButton: MaterialButton

    // A button to go to the next fragment
    lateinit var nextButton: MaterialButton

    // A button to close the connection with the current device
    lateinit var closeButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_device_control)
//
//        // Get the connection device models from the intent extras using the inline function
//        connectionDeviceModels = intent.getSerializableExtraSafe("connectionDeviceModels") ?: ArrayList()
//
//        // Initialize the view pager and its adapter
//        viewPager = findViewById(R.id.view_pager)
//        viewPager.adapter = DeviceControlPagerAdapter(this, connectionDeviceModels)
//
//        // Initialize the tab layout and attach it to the view pager
//        tabLayout = findViewById(R.id.tab_layout)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            // Set the tab text from the device name
//            tab.text = connectionDeviceModels[position].deviceItemType.name
//        }.attach()
//
//        // Initialize the prev button and set its click listener
//        prevButton = findViewById(R.id.prev_button)
//        prevButton.setOnClickListener {
//            // Go to the previous fragment if possible
//            val currentItem = viewPager.currentItem
//            if (currentItem > 0) {
//                viewPager.currentItem = currentItem - 1
//            }
//        }
//
//        // Initialize the next button and set its click listener
//        nextButton = findViewById(R.id.next_button)
//        nextButton.setOnClickListener {
//            // Go to the next fragment if possible
//            val currentItem = viewPager.currentItem
//            if (currentItem < adapter.itemCount - 1) {
//                viewPager.currentItem = currentItem + 1
//            }
//        }
//
//        // Initialize the close button and set its click listener
//        closeButton = findViewById(R.id.close_button)
//        closeButton.setOnClickListener {
//            // Remove the current item from the adapter and notify the change
//            val currentItem = viewPager.currentItem
//            adapter.removeItem(currentItem)
//            // If there are no more items left, finish the activity
//            if (adapter.itemCount == 0) {
//                finish()
//            }
//        }
    }

    // Define an inline function with a reified type parameter to get the serializable extra safely
    private inline fun <reified T : Serializable> Intent.getSerializableExtraSafe(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Use the new method with the class parameter
            getSerializableExtra(key, T::class.java)
        } else {
            // Use the old method and cast the result with as? operator
            @Suppress("DEPRECATION")
            getSerializableExtra(key) as? T
        }
    }
}