package ru.hse.control_system_v2.ui.widgets.exoplayer

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.ui.StyledPlayerView
import ru.hse.control_system_v2.R

class PlayerView: FrameLayout, DefaultLifecycleObserver {
    private lateinit var playerView: StyledPlayerView
    lateinit var player: ExoPlayer
    private lateinit var path: String


    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        LayoutInflater.from(context).inflate(R.layout.player_view, this)

        playerView = findViewById(R.id.exo_player_view)

        player = ExoPlayer.Builder(context).build()

        playerView.player = player

        setPath("https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4")

        val lifecycle = (context as? LifecycleOwner)?.lifecycle ?: return

        lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        player.playWhenReady = true
    }

    override fun onStop(owner: LifecycleOwner) {
        player.playWhenReady = false
    }

    override fun onDestroy(owner: LifecycleOwner) {
        player.release()
    }

    public fun setPath(path: String){
        this.path = path
        val mediaItem = MediaItem.fromUri(Uri.parse(path))

        val mediaSourceFactory = DefaultMediaSourceFactory(context)

        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

        player.setMediaSource(mediaSource)
    }
}