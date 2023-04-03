package ru.hse.control_system_v2.ui.settings

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.file.FileParse
import ru.hse.control_system_v2.databinding.FragmentSettingsBinding
import ru.hse.control_system_v2.ui.protocol.NavigationDialog

private const val REQUEST_CODE_PERMISSION = 100 // Код запроса разрешения
private const val REQUEST_CODE_PICK_FILE = 101 // Код запроса выбора файла
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

        // Найти кнопку по id в разметке
        val buttonPickFile = dataBinding.buttonPickFile
        // Установить слушатель нажатия на кнопку
        buttonPickFile.setOnClickListener {
            // Вызвать функцию для проверки и запроса разрешения на чтение внешнего хранилища
            checkAndRequestPermission()
        }
    }

    // Перенести логику проверки и запроса разрешения в отдельную функцию
    private fun checkAndRequestPermission() {
        // Проверить наличие разрешения на чтение внешнего хранилища
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            // Если разрешения нет, запросить его у пользователя
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        } else {
            // Если разрешение уже есть, вызвать функцию для выбора xml файла из памяти
            pickXmlFile()
        }
    }

    // Обработать результат запроса разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            // Если пользователь дал разрешение, продолжить работу
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "Разрешение получено", Toast.LENGTH_SHORT).show()
            } else {
                // Если пользователь отказал в разрешении, показать сообщение
                Toast.makeText(requireContext(), "Разрешение необходимо для работы приложения", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Вызвать эту функцию, когда пользователь нажимает на элемент меню для выбора файла
    private fun pickXmlFile() {
        // Создать намерение для выбора xml файла из памяти
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "text/xml" // Установить тип данных для фильтрации файлов
            addCategory(Intent.CATEGORY_OPENABLE) // Добавить категорию для открытия файлов
        }
        // Запустить намерение с кодом запроса
        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
    }

    // Обработать результат выбора файла и получить Uri файла
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_FILE && resultCode == Activity.RESULT_OK) {
            // Получить Uri выбранного файла из данных намерения
            val fileUri = data?.data
            if (fileUri != null) {
                // Вызвать функцию для загрузки xml файла из памяти по Uri
                loadXmlFile(fileUri)
            } else {
                Toast.makeText(requireContext(), "Файл не выбран", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadXmlFile(fileUri: Uri) {
        // Реализовать логику для загрузки xml файла из памяти по Uri
        CoroutineScope(Dispatchers.Main).launch {
            // Suspend the coroutine until the lifecycle is DESTROYED.
            // repeatOnLifecycle launches the block in a new coroutine every time the
            // lifecycle is in the STARTED state (or above) and cancels it when it's STOPPED.
            val protocol = FileParse.parseXml(FileParse.getXmlFromUrl(fileUri))
            val b = Bundle()
            b.putSerializable("protocol", protocol)
            Navigation.findNavController(requireParentFragment().requireView())
                .navigate(R.id.action_settingsFragment_to_navigationDialog, b)
        }

    }

}