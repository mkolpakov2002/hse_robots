package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Полезная нагрузка запроса на уведомление об изменении состояния устройств.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
data class CallbackStatesRequestPayload(
    @SerialName("user_id") val userId: String,
    val devices: List<DeviceState>
)

/**
 * Запрос на уведомление об изменении состояния устройств.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
data class CallbackStatesRequest(
    val ts: Long = System.currentTimeMillis() / 1000,
    val payload: CallbackStatesRequestPayload
): APIModel

/**
 * Полезная нагрузка запроса на уведомление об изменении параметров устройств.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
data class CallbackDiscoveryRequestPayload(
    @SerialName("user_id") val userId: String
)

/**
 * Запрос на уведомление об изменении параметров устройств.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
data class CallbackDiscoveryRequest(
    val ts: Long = System.currentTimeMillis() / 1000,
    val payload: CallbackDiscoveryRequestPayload
): APIModel

/**
 * Статус ответа на запрос обратного вызова.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
enum class CallbackResponseStatus {
    OK, ERROR
}

/**
 * Ответ на запрос обратного вызова.
 * https://yandex.ru/dev/dialogs/smart-home/doc/reference-alerts/resources-alerts.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/callback.py
 */
@Serializable
data class CallbackResponse(
    val status: CallbackResponseStatus,
    @SerialName("error_code") val errorCode: String? = null,
    @SerialName("error_message") val errorMessage: String? = null
): APIModel