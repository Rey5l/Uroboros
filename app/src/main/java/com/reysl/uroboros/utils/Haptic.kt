package com.reysl.uroboros.utils

import android.Manifest
import android.os.Build
import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresPermission

@RequiresPermission(Manifest.permission.VIBRATE)
fun performHapticClick(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    vibrator.vibrate(
        VibrationEffect.createOneShot(
            10,
            90
        )
    )
}

@RequiresPermission(Manifest.permission.VIBRATE)
fun performHapticTick(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val effect = VibrationEffect.createWaveform(
            longArrayOf(0, 10, 20, 10),
            intArrayOf(0, 90, 0, 110),
            -1
        )
        vibrator.vibrate(effect)
    }
}