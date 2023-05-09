package ru.hse.control_system_v2.ui.connection.video

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import ru.hse.control_system_v2.R
import ru.hse.control_system_v2.data.classes.device.model.VideoModel

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    // Ссылки на виджеты внутри CardView
    private val videoPlayer: StyledPlayerView = itemView.findViewById(R.id.video_player)
    private val videoName: TextView = itemView.findViewById(R.id.video_name)

    // Переменная для хранения ExoPlayer
    private var player: ExoPlayer? = null

    // Интерфейс для передачи информации о выбранном видео в родительский фрагмент
    interface OnVideoClickListener {
        fun onVideoClick(video: VideoModel)
    }

    // Метод для связывания данных с виджетами
    fun bind(video: VideoModel, listener: OnVideoClickListener) {
        // Здесь ты можешь заполнить виджеты данными из объекта video
        // Например, ты можешь установить текст для videoName из name
        videoName.text = video.name

        // Здесь ты можешь создать и настроить ExoPlayer для проигрывания видео из mediaItem или connectionIp
        player = ExoPlayer.Builder(itemView.context).build()
        videoPlayer.player = player as ExoPlayer
        val mediaSource = ProgressiveMediaSource.Factory(DefaultHttpDataSource.Factory())
            .createMediaSource(MediaItem.fromUri(video.connectionIp))
        player?.setMediaSource(mediaSource)
        player?.prepare()
        player?.playWhenReady = true

    }
}