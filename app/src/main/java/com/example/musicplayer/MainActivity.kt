package com.example.musicplayer

import android.content.Intent
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

        //액션바 설정
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val resourceId = R.raw.test1

        val retriever = MediaMetadataRetriever()
        val uriString = "android.resource://${packageName}/${resourceId}"
        val uri = Uri.parse(uriString)
        retriever.setDataSource(this, uri)

        //메타 데이터 설정
        val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
        val musicTitle1 = findViewById<TextView>(R.id.musicTitle1)
        val musicTimer1 = findViewById<TextView>(R.id.musicTimer1)
        val musicArtist = findViewById<TextView>(R.id.musicArtist)
        musicTitle1.text = title ?: "Unknown Title"
        musicArtist.text = artist ?: "Unknown Artist"
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
            val intent = Intent(this, PlayerActivity::class.java)
            startActivity(intent)
        }
    }



    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacksAndMessages(null)
    }
}
