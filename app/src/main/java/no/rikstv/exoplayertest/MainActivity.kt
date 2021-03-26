package no.rikstv.exoplayertest

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.mux.stats.sdk.core.model.CustomerPlayerData
import com.mux.stats.sdk.core.model.CustomerVideoData
import com.mux.stats.sdk.muxstats.MuxStatsExoPlayer
import no.rikstv.exoplayertest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var binding: ActivityMainBinding? = null
    private var player: SimpleExoPlayer? = null
    private var muxStats: MuxStatsExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
        }

        val mediaItem = MediaItem
            .Builder()
            .setUri("https://storage.googleapis.com/wvmedia/clear/hevc/tears/tears.mpd")
            .build()
        initializePlayer(mediaItem)
    }


    private fun releasePlayer() {
        player?.apply {
            release()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT <= 23) {
            binding?.playerView?.onPause()
        }
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Build.VERSION.SDK_INT > 23) {
            binding?.playerView?.onPause()
        }
        releasePlayer()
    }

    private fun initializePlayer(mediaItem: MediaItem) {
        val trackSelector = DefaultTrackSelector(this)
        player = SimpleExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build().apply {
                playWhenReady = true
            }

        val customerPlayerData = CustomerPlayerData()
        customerPlayerData.environmentKey = "YOUR KEY HERE"
        val customerVideoData = CustomerVideoData()
        customerVideoData.videoTitle = "test"

        muxStats = MuxStatsExoPlayer(
            this,
            player,
            "android-test-player",
            customerPlayerData,
            customerVideoData
        ).apply {
            this.setPlayerView(binding!!.playerView)
        }
        binding?.playerView?.player = player
        player?.setMediaItem(mediaItem)
        player?.prepare()
    }
}
