package com.example.myproject

import android.content.Context
import android.media.MediaPlayer

open class AudioPlay {
        var mediaPlayer: MediaPlayer? = null
        var isplayingAudio = false
        fun playAudio(c: Context?) {
           mediaPlayer = MediaPlayer.create(c,R.raw.destinationarrivedvoice)
            if (!isplayingAudio){
                mediaPlayer!!.start()
                mediaPlayer!!.isLooping = true
                isplayingAudio = true
            }
        }

        fun stopAudio() {
            isplayingAudio = false
            mediaPlayer!!.stop()
        }
}