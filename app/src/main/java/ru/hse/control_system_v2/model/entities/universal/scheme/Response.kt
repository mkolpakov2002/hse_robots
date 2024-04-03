package ru.hse.control_system_v2.model.entities.universal.scheme

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Код ответа.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/response-codes.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/response.py
 */
@Serializable
enum class ResponseCode {
    DOOR_OPEN, LID_OPEN, REMOTE_CONTROL_DISABLED, NOT_ENOUGH_WATER, LOW_CHARGE_LEVEL,
    CONTAINER_FULL, CONTAINER_EMPTY, DRIP_TRAY_FULL, DEVICE_STUCK, DEVICE_OFF,
    FIRMWARE_OUT_OF_DATE, NOT_ENOUGH_DETERGENT, HUMAN_INVOLVEMENT_NEEDED, DEVICE_UNREACHABLE,
    DEVICE_BUSY, INTERNAL_ERROR, INVALID_ACTION, INVALID_VALUE, NOT_SUPPORTED_IN_CURRENT_MODE,
    ACCOUNT_LINKING_ERROR, DEVICE_NOT_FOUND
}

/**
 * Базовый класс для полезной нагрузки ответа API.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/response-codes.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/response.py
 */
@Serializable
open class ResponsePayload: APIModel

/**
 * Полезная нагрузка ошибки.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/response-codes.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/response.py
 */
@Serializable
data class Error(
    @SerialName("error_code") val errorCode: ResponseCode,
    @SerialName("error_message") val errorMessage: String? = null
): ResponsePayload()

/**
 * Базовый ответ API.
 * https://yandex.ru/dev/dialogs/smart-home/doc/concepts/response-codes.html
 * https://raw.githubusercontent.com/dext0r/yandex_smart_home/master/custom_components/yandex_smart_home/schema/response.py
 */
@Serializable
sealed interface Response: APIModel {
    @SerialName("request_id")
    val requestId: String?
    val payload: ResponsePayload?
}