package ru.hse.control_system_v2.data.classes.device.model

import com.google.android.exoplayer2.MediaItem
import java.io.Serializable

/**
 * Model класс видео устройства
 */
class VideoModel(
    override var id: Int = 0,
    override var name: String = "",
    override var connectionIp: String = "",
    override var connectionPort: Int = 0,
    override var currentWindow: Int = 0,
    override var playbackPosition: Long = 0,
    override var isFullscreen: Boolean = false,
    override var isPlayerPlaying: Boolean = false,
    override val mediaItem: MediaItem? = null
)
    : ItemType,
    Serializable,
    VideoConnectable {

}