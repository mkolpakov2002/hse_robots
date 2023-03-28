package ru.hse.control_system_v2.ui.connection

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import ru.hse.control_system_v2.R

// Класс фрагмента для отображения произвольного количества ExoPlayer
class VideoFragment : Fragment() {

    // Ключ для передачи списка URL видео в аргументах фрагмента
    companion object {
        const val VIDEO_URL_LIST_KEY = "videoUrlList"
    }

    // Поле для хранения списка URL видео
    private var videoUrlList: List<String>? = null

    // Поле для хранения списка экземпляров ExoPlayer
    private var playerList: MutableList<ExoPlayer> = mutableListOf()

    // Поле для хранения списка представлений ExoPlayer
    private var playerViewList: MutableList<StyledPlayerView> = mutableListOf()

    // Фабричный метод для создания нового экземпляра фрагмента с заданным списком URL видео
    fun newInstance(videoUrlList: List<String>): VideoFragment {
        val fragment = VideoFragment()
        val args = Bundle()
        args.putStringArrayList(VIDEO_URL_LIST_KEY, ArrayList(videoUrlList))
        fragment.arguments = args
        return fragment
    }

    // Метод для получения списка URL видео из аргументов фрагмента
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        videoUrlList = arguments?.getStringArrayList(VIDEO_URL_LIST_KEY)
    }

    // Метод для создания представления фрагмента и инициализации полей playerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        val playerContainer = view.findViewById<LinearLayout>(R.id.video_container)
        videoUrlList?.forEach { _ ->
            val playerView = StyledPlayerView(requireContext())
            playerView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            playerContainer.addView(playerView)
            playerViewList.add(playerView)
        }
        return view
    }

    // Метод для создания и настройки экземпляров ExoPlayer при старте фрагмента
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayers()
        }
    }

    // Метод для создания и настройки экземпляров ExoPlayer при возобновлении фрагмента
    override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || playerList.isEmpty()) {
            initializePlayers()
        }
    }

    // Метод для освобождения ресурсов ExoPlayer при приостановке фрагмента
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayers()
        }
    }

    // Метод для освобождения ресурсов ExoPlayer при остановке фрагмента
    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayers()
        }
    }

    // Метод для создания и настройки экземпляров ExoPlayer
    private fun initializePlayers() {
        videoUrlList?.forEachIndexed { index, videoUrl ->
            val player = ExoPlayer.Builder(requireContext()).build()
            val playerView = playerViewList[index]
            playerView.player = player

            val uri = Uri.parse(videoUrl)
            val mediaSource = buildMediaSource(uri)

            player.playWhenReady = true
            player.seekTo(0)
            player.prepare(mediaSource, false, false)

            playerList.add(player)
        }

    }

    // Метод для построения источника медиа из URI
    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSourceFactory =
            DefaultDataSourceFactory(requireContext(), "exoplayer-sample")
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
    }

    // Метод для освобождения ресурсов ExoPlayer
    private fun releasePlayers() {
        playerList.forEach { player ->
            player.playWhenReady = false
            player.release()
        }
        playerList.clear()
    }
}