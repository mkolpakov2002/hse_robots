package ru.hse.control_system_v2.ui.fragments.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.databinding.FragmentSettingsBinding

private const val REQUEST_CODE_PERMISSION = 100
private const val REQUEST_CODE_PICK_FILE = 101
class SettingsFragment : Fragment(){
    private var fragmentContext: Context? = null
    private val dataBinding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onAttach(context: Context) {
        fragmentContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recyclerView = dataBinding.settingItems
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = SettingsAdapter(getSettingsList()) {
            settingsItemModel ->
            when(settingsItemModel.itemId){
                1 -> {
                    checkAndRequestPermission()
                }
                2 -> {

                }
                else -> {

                }
            }
        }
    }

    private fun getSettingsList(): List<SettingsItemModel> {
        return listOf(
            SettingsItemModel(1, "Загрузка файла конфигурации протокола", R.drawable.ic_baseline_upload_file_24),
            SettingsItemModel(2, "Выбор темы оформления", R.drawable.ic_application_theme),
        )
    }

    // Перенести логику проверки и запроса разрешения в отдельную функцию
    private fun checkAndRequestPermission() {
        // Проверить наличие разрешения на чтение внешнего хранилища
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Если разрешения нет, запросить его у пользователя
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            //TODO
            // Если разрешение уже есть, вызвать функцию для выбора xml файла из памяти
        }
    }

}