package com.example.musicplayer

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var handler: Handler
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val resourceId = R.raw.test1

        val retriever = MediaMetadataRetriever()
        val uriString = "android.resource://${packageName}/${resourceId}"
        val uri = Uri.parse(uriString)
        retriever.setDataSource(this, uri)

        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val musicTitle1 = findViewById<TextView>(R.id.musicTitle1)
        val musicTimer1 = findViewById<TextView>(R.id.musicTimer1)
        musicTitle1.text = title ?: "Unknown Title"
        val durationLong = duration?.toLongOrNull() ?: 0L
        val durationMinutes = durationLong / 1000 / 60
        val durationSeconds = durationLong / 1000 % 60
        musicTimer1.text = String.format("%02d:%02d", durationMinutes, durationSeconds)

        mediaPlayer = MediaPlayer.create(this, resourceId)
        seekBar = findViewById(R.id.seekBar)
        seekBar.max = mediaPlayer.duration

        handler = Handler()

        mediaPlayer.setOnCompletionListener {
            mediaPlayer.release()
            isPlaying = false
        }

        val firstMusicLayout = findViewById<LinearLayout>(R.id.firstMusic)
        firstMusicLayout.setOnClickListener {
            if (isPlaying) {
                mediaPlayer.pause()
                isPlaying = false
            } else {
                mediaPlayer.start()
                isPlaying = true
                updateSeekBar()
            }

            //가수 -> 플레이어 바
            val musicArtist = findViewById<TextView>(R.id.musicArtist)
            val seekbar = findViewById<SeekBar>(R.id.seekBar)

            musicArtist.visibility = View.GONE
            seekbar.visibility = View.VISIBLE

            val imageView = findViewById<ImageView>(R.id.imageView)
            var isIconChanged = false


                if(isIconChanged){
                    imageView.setImageResource(R.drawable.ready)

                }else {
                    imageView.setImageResource(R.drawable.play)
                }


            // 배경색 변경
            val background = findViewById<LinearLayout>(R.id.firstMusic)
            if (isPlaying) {
                background.setBackgroundColor(resources.getColor(R.color.bgYellow)) // 배경색을 변경할 색상으로 설정하세요.
            } else {
                background.setBackgroundColor(resources.getColor(android.R.color.white)) // 배경색을 변경할 색상으로 설정하세요.
            }
        }


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
        val musicTimer1 = findViewById<TextView>(R.id.musicTimer1)
        musicTimer1.text = String.format("%02d:%02d", minutesLeft, secondsLeft)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}
