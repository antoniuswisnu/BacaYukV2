package com.nara.bacayuk.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.util.Log

fun playAudioFromRawAssets(context: Context, fileName: Int) {
    val mediaPlayer = MediaPlayer.create(context, fileName)
    mediaPlayer.start()
}

fun playAudioFromRawAssetsFileString(context: Context, fileName: String){
    val resourceId = context.resources.getIdentifier(fileName, "raw", context.packageName)
    if (resourceId != 0) {
        val mediaPlayer = MediaPlayer.create(context, resourceId)
        mediaPlayer.start()
    } else {
        Log.e("AudioPlayer", "File not found: $fileName")
    }
}

fun playAudioFromUrl(url: String) {
    val mediaPlayer = MediaPlayer()
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

    try{
        mediaPlayer.apply {
            setDataSource(url)
            prepare()
            start()
        }
    } catch (e: Exception){
        e.printStackTrace()
    }
}