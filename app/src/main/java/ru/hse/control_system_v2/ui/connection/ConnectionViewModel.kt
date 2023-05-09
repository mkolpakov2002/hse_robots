package ru.hse.control_system_v2.ui.connection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hse.control_system_v2.connection.ConnectionClass

class ConnectionViewModel : ViewModel(){
    // Создаем LiveData для хранения состояния соединения
    private val _connectionState = MutableLiveData<ConnectionClass.ConnectionState>()
    val connectionState: LiveData<ConnectionClass.ConnectionState>
        get() = _connectionState

    // Создаем LiveData для хранения ошибок
    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error
}