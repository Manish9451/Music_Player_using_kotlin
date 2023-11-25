package com.example.musicplayer

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

//    private lateinit var mediaPlayer: MediaPlayer
//    private lateinit var playButton: ImageButton
//    private lateinit var seekBar: SeekBar
//    private var isPlaying: Boolean = false

    private lateinit var playButton: ImageButton
    private lateinit var seekBar: SeekBar
    private var mediaPlayer: MediaPlayer? = null
    private var currentSongIndex = 0
    private var rotationAnimation: Animation? = null

    private val songs = listOf(
        R.raw.music2 to R.drawable.img_1,
        R.raw.music to R.drawable.img4,
        R.raw.music1 to R.drawable.img2
        // Add more song resources here
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.imageView)

//        val rotationAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_animation)
//        imageView.startAnimation(rotationAnimation)
        playButton = findViewById(R.id.play_btn)
        val previousButton = findViewById<ImageButton>(R.id.previous_btn)
        val nextButton = findViewById<ImageButton>(R.id.next_btn)
        seekBar = findViewById(R.id.seekbar)

        playButton.setOnClickListener {
            if (mediaPlayer == null) {
                prepareMediaPlayer()
                mediaPlayer?.start()
                playButton.setImageResource(R.drawable.baseline_pause_24)
            } else {
                if (mediaPlayer?.isPlaying == true) {
                    mediaPlayer?.pause()
                    playButton.setImageResource(R.drawable.baseline_play_arrow_24)
//                    stopRotationAnimation()  // Stop rotation animation when pause button is clicked

                } else {
                    mediaPlayer?.start()
                    playButton.setImageResource(R.drawable.baseline_pause_24)
//                    startRotationAnimation()
                }
            }
        }

        previousButton.setOnClickListener {
            playPreviousSong()
        }

        nextButton.setOnClickListener {
            playNextSong()
        }
        val timeTextView = findViewById<TextView>(R.id.timeTextView)
        val updateSeekBar = Runnable {
            val currentPosition = mediaPlayer?.currentPosition ?: 0
            val fullDuration = mediaPlayer?.duration ?: 0
            val currentPositionFormatted = formatDuration(currentPosition)
            val fullDurationFormatted = formatDuration(fullDuration)
            timeTextView.text = "Start: $currentPositionFormatted                          Duration: $fullDurationFormatted"
            seekBar.progress = currentPosition
        }

        Thread {
            while (true) {
                try {
                    Thread.sleep(1000)
                    runOnUiThread(updateSeekBar)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

//    private fun startRotationAnimation() {
//        val imageView = findViewById<ImageView>(R.id.imageView)
//        imageView.startAnimation(rotationAnimation)
//    }
//
//    private fun stopRotationAnimation() {
//        val imageView = findViewById<ImageView>(R.id.imageView)
//        imageView.clearAnimation()
//    }


    private fun prepareMediaPlayer() {
        val song = songs[currentSongIndex]
        mediaPlayer = MediaPlayer.create(this, song.first)
        mediaPlayer?.setOnCompletionListener {
            playNextSong()
        }
        seekBar.max = mediaPlayer?.duration ?: 0

        // Set the image corresponding to the current song

        val imageView = findViewById<ImageView>(R.id.imageView)
        imageView.setImageResource(songs[currentSongIndex].second)

    }

    private fun playNextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        mediaPlayer?.release()
        prepareMediaPlayer()
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.baseline_pause_24)
    }

    private fun playPreviousSong() {
        currentSongIndex = if (currentSongIndex == 0) songs.size - 1 else currentSongIndex - 1
        mediaPlayer?.release()
        prepareMediaPlayer()
        mediaPlayer?.start()
        playButton.setImageResource(R.drawable.baseline_pause_24)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }

    private fun formatDuration(duration: Int): String {
        val seconds = duration / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }
}