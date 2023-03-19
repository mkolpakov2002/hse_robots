package ru.hse.control_system_v2.data.classes.device.model

import android.net.InetAddresses
import com.google.android.exoplayer2.MediaItem

/**
 * логика простого видео устройства
 */
interface VideoConnectable {
    var connectionIp: String
    var connectionPort: Int
    var currentWindow: Int
    var playbackPosition: Long
    var isFullscreen: Boolean
    var isPlayerPlaying: Boolean
    val mediaItem: MediaItem?
    val isWiFiConnectable: Boolean
        get() = connectionIp.let {  InetAddresses.isNumericAddress(it) }
}