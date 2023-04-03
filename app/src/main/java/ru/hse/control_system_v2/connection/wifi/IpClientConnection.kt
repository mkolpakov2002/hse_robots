package ru.hse.control_system_v2.connection.wifi

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import ru.hse.control_system_v2.connection.ConnectionClass
import ru.hse.control_system_v2.data.classes.device.model.DeviceModel
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking

/**
 * Класс клиентского типа соединения
 */
open class IpClientConnection(deviceItemType: DeviceModel, connectionName: String?) :
    ConnectionClass<DefaultClientWebSocketSession?>(deviceItemType, connectionName) {

    private lateinit var client: HttpClient

    override suspend fun sentData(data: ByteArray) {
        socket?.send(data)
    }

    override suspend fun closeConnection() {
        coroutine?.cancel()
        client.close()
    }

    override suspend fun openConnection(): Unit = runBlocking(Dispatchers.IO) {
        client = HttpClient(Android){
            install(ContentNegotiation) {
                json()
            }
        }

        // Определяем таймаут для соединения в миллисекундах
        val timeout = 5000L

        // Внутри блока launch выполняем код для коммуникации с сервером
        // Пытаемся установить соединение с сервером в блоке try-catch
        try {
            // Создаем объект сессии веб-сокета с помощью функции client.webSocketSession
            socket = client.webSocketSession {
                url {
                    protocol = URLProtocol.WS
                    host = deviceItemType.wifiAddress
                    port = deviceItemType.port
                }
            }

            // Устанавливаем таймаут для сессии
            socket?.timeoutMillis = timeout

            // Отправляем сообщение серверу с помощью функции socket.send
            socket?.send("Hello from Android")

            // Получаем сообщения от сервера в цикле while, пока сессия активна и корутина не отменена
            while (socket != null && socket?.isActive == true && isActive) {
                // Получаем сообщение с помощью функции socket.incoming.receiveOrNull
                val message = socket?.incoming?.receiveCatching()?.getOrNull()
                connectionState = isAlive
                // Проверяем тип сообщения и приводим его к строке или байтам
                when (message) {
                    is Frame.Text -> {
                        val text = message.readText()
                        // Выводим текст в лог или UI
                        Log.d("MainActivity", "Received text: $text")
                    }
                    is Frame.Binary -> {
                        val bytes = message.readBytes()
                        // Обрабатываем байты как нужно
                        Log.d("MainActivity", "Received bytes: ${bytes.size}")
                    }
                    else -> {
                        // Ничего не делаем или выходим из цикла
                        break
                    }
                }
            }

            // Закрываем соединение с сервером с помощью функции socket.close
            socket?.close()

        } catch (e: Exception) {
            // В случае исключения выводим его в лог или UI
            Log.e("MainActivity", "Error: ${e.message}")
        } finally {
            // В конце закрываем клиент Ktor с помощью функции client.close
            client.close()
            connectionState = isOnError
        }
    }

    override suspend fun read(buffer: ByteArray, bytesRead: Int): Int {
        TODO("Not yet implemented")
    }


}