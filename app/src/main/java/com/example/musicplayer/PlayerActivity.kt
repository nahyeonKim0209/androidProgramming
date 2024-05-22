package com.example.musicplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity

class PlayerActivity : AppCompatActivity() {
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var handler: Handler
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_view)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val resourceId = R.raw.test1

        val retriever = MediaMetadataRetriever()
        val uriString = "android.resource://${packageName}/${resourceId}"
        val uri = Uri.parse(uriString)
        retriever.setDataSource(this, uri)

        //메타 데이터 설정
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val playTitle = findViewById<TextView>(R.id.playTitle)
        val playDuration = findViewById<TextView>(R.id.playDuration)
        playTitle.text = title ?: "Unknown Title"
        val durationLong = duration?.toLongOrNull() ?: 0L
        val durationMinutes = durationLong / 1000 / 60
        val durationSeconds = durationLong / 1000 % 60
        playDuration.text = String.format("%02d:%02d", durationMinutes, durationSeconds)

        mediaPlayer = MediaPlayer.create(this, resourceId)
        seekBar = findViewById(R.id.playSeekBar)
        seekBar.max = mediaPlayer.duration

        handler = Handler()

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            isPlaying = false
        }

        // 음악 재생 시작
        mediaPlayer.start()
        isPlaying = true
        updateSeekBar()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateSeekBar() {
        seekBar.progress = mediaPlayer.currentPosition
        handler.postDelayed({ updateSeekBar() }, 1000)
        val durationLeft = mediaPlayer.duration - mediaPlayer.currentPosition
        val minutesLeft = durationLeft / 1000 / 60
        val secondsLeft = durationLeft / 1000 % 60
        val musicTimer = findViewById<TextView>(R.id.playDuration)
        musicTimer.text = String.format("%02d:%02d", minutesLeft, secondsLeft)
    }

    override fun onBackPressed() {
        if (isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
            isPlaying = false
        }
        handler.removeCallbacksAndMessages(null)
        super.onBackPressed()
    }
    override fun onDestroy() {
        super.onDestroy()
        if (isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
            isPlaying = false
        }
        handler.removeCallbacksAndMessages(null)
    }
}