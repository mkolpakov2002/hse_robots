package ru.hse.control_system_v2.ui.fragments.connection_settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.ktor.client.*
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.model.entities.DeviceOld
import ru.hse.control_system_v2.databinding.FragmentConnectionTypeBinding
import ru.hse.control_system_v2.databinding.ItemConnectionTypeBinding
import ru.hse.control_system_v2.model.entities.universal.YandexSmartHomeAPITest

class ConnectionTypeFragment : Fragment() {

    private val viewModel: ConnectionTypeViewModel by lazy {
        ViewModelProvider(this)[ConnectionTypeViewModel::class.java]
    }
    private lateinit var binding: FragmentConnectionTypeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConnectionTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabConnect.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val apiTest = YandexSmartHomeAPITest()
                apiTest.testAll()
            }
        }
    }


}