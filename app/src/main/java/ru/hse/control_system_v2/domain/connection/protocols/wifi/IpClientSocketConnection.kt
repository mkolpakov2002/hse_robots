package ru.hse.control_system_v2.domain.connection.protocols.wifi

import android.util.Log
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.Dispatchers
import ru.hse.control_system_v2.domain.connection.ConnectionClass
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hse.control_system_v2.model.entities.ConnectionDeviceModel

/**
 * Класс клиентского типа соединения
 */
open class IpClientSocketConnection(connectionDeviceModel: ConnectionDeviceModel) :
    ConnectionClass<DefaultClientWebSocketSession?>(connectionDeviceModel) {

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    override suspend fun sendData(data: ByteArray) {
        socket?.send(data)
    }

    override suspend fun closeConnection() {
        coroutine?.cancel()
        client.close()
    }
    // Определяем интерфейс для разных алгоритмов обработки сообщений
    interface MessageHandler {
        fun handle(message: Frame)
    }

    // Реализуем конкретные алгоритмы для текстовых и бинарных сообщений
    class TextMessageHandler : MessageHandler {
        override fun handle(message: Frame) {
            // Проверяем, что сообщение является текстовым
            if (message is Frame.Text) {
                val text = message.readText()
                Log.d("MainActivity", "Received text: $text")
            }
        }
    }

    class BinaryMessageHandler : MessageHandler {
        override fun handle(message: Frame) {
            // Проверяем, что сообщение является бинарным
            if (message is Frame.Binary) {
                val bytes = message.readBytes()
                Log.d("MainActivity", "Received bytes: ${bytes.size}")
            }
        }
    }

    // Создаем класс контекста, который хранит ссылку на текущий алгоритм
    class MessageContext(var handler: MessageHandler) {
        // Вызываем метод обработки сообщения у текущего алгоритма
        fun processMessage(message: Frame) {
            handler.handle(message)
        }
    }

    // В методе openConnection() создаем экземпляры алгоритмов и контекста
    override suspend fun openConnection() {
        val timeout = 5000L
        try {
            socket = client.webSocketSession {
                url {
                    protocol = URLProtocol.WS
                    host = connectionDeviceModel.deviceItemType.wifiAddress
                    port = connectionDeviceModel.deviceItemType.port
                }
            }
            socket?.timeoutMillis = timeout
            coroutine = CoroutineScope(Dispatchers.IO).launch {
                socket?.send("Hello from Android")
                // Создаем объекты для обработки текстовых и бинарных сообщений
                val textHandler = TextMessageHandler()
                val binaryHandler = BinaryMessageHandler()
                // Создаем контекст и устанавливаем начальный алгоритм
                val context = MessageContext(textHandler)
                while (socket != null && socket?.isActive == true && isActive) {
                    val message = socket?.incoming?.receiveCatching()?.getOrNull()
                    updateState(ConnectionState.ALIVE)
                    when (message) {
                        is Frame.Text -> {
                            // Используем алгоритм для текстовых сообщений
                            context.handler = textHandler
                            context.processMessage(message)
                        }
                        is Frame.Binary -> {
                            // Используем алгоритм для бинарных сообщений
                            context.handler = binaryHandler
                            context.processMessage(message)
                        }
                        else -> {
                            // Ничего не делаем или выходим из цикла
                            break
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error: ${e.message}")
        } finally {
            updateState(ConnectionState.DISCONNECTED)
            client.close()
        }
    }

    override suspend fun read(buffer: ByteArray): Int {
        TODO("Not yet implemented")
    }

}