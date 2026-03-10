package com.example.learningwords

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat

class VolumeManager(private val context: Context, private val seekBar: VerticalSeekBar, private val init: UiInitializer, private val data: DataManager) {

    private val handler = Handler(Looper.getMainLooper())
    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // Добавьте переменную для хранения изначального уровня громкости
    private var originalVolume: Int = -1

    val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)

    init {
        seekBar.max = maxVolume
        seekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

//        seekBar.visibility = View.GONE

        setSeekBarListener()
        configureSeekBar()

        if (data.isVolume_)
            showSeekBarWithAnimation() // показываем на экране виджет громкости
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setSeekBarListener() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Nothing needed here
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // открываем seekBar и buttonVolume
                handler.postDelayed({
                    hideSeekBarWithAnimation()
                }, 1000) // Delay for the SeekBar to auto-hide
            }
        })
        seekBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN)
                handler.removeCallbacksAndMessages(null)
            false
        }
    }

    @SuppressLint("ResourceAsColor")
    private fun hideSeekBarWithAnimation() {

        //--- анимация для сик-бара
        seekBar.animate()
            .translationX(-seekBar.width.toFloat())
            .alpha(0f)
            .setDuration(500)
            .withEndAction {
//                seekBar.visibility = View.GONE
            }
            .start()
    }

    // открываем seekBar и buttonVolume
    fun showSeekBarWithAnimation() {
        // открываем seekBar
        seekBar.visibility = View.VISIBLE
        seekBar.animate()
            .translationX(0f)
            .alpha(1f)
            .setDuration(500)
            .start()

        // после задержки закрываем seekBar и buttonVolume
        closeVolumeWidget ()
    }

    // закрыть виджет громкости
    fun closeVolumeWidget () {
        // после задержки закрываем seekBar и buttonVolume
        handler.postDelayed({
            hideSeekBarWithAnimation()
        }, 3000) // Delay for the SeekBar to auto-hide
    }

    private fun configureSeekBar() {
        val thumbColor = ContextCompat.getColor(context, R.color.red) // используем context вместо this
        val thumb = GradientDrawable()
        thumb.shape = GradientDrawable.RECTANGLE
        thumb.setColor(thumbColor) // меняем цвет ползунка
        thumb.setSize(16, 48) // в пикселях
        seekBar.thumb = thumb
        val progressDrawableColor = ContextCompat.getColor(context, R.color.text_color) // цвет для дорожки ползунка
        seekBar.progressDrawable.setColorFilter(progressDrawableColor, PorterDuff.Mode.SRC_IN) // меняем цвет дорожки
        seekBar.max = maxVolume
        seekBar.progress = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

//        setVolume30PercentHigher()
//        increaseVolume()
    }

//    fun increaseVolume() {
//        if (originalVolume == -1) {
//            // Сначала сохраняем текущий уровень громкости
//            originalVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
//            Log.e("aaa","originalVolume-----------------=$originalVolume")
//        }
//
//        val volumeIncrease = (maxVolume * 0.3).toInt() // Рассчитываем увеличение на 30%
//        val newVolume = (originalVolume + volumeIncrease).coerceAtMost(maxVolume) // Не превышаем maxVolume
//        Log.e("aaa","newVolume-----------------=$newVolume")
//        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, 0)
//        seekBar.progress = newVolume // Обновить ползунок
//    }
//
//    fun restoreOriginalVolume() {
//        if (originalVolume >= 0) {
//            // Возвращаем предыдущий уровень громкости
//            Log.e("aaa","originalVolume-----------------2=$originalVolume")
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
//            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
//            Log.e("aaa","originalVolumeEnd-----------------=${audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)}")
//            seekBar.progress = originalVolume // Обновить ползунок
//            originalVolume = -1 // Сбросить значение, чтобы снова можно было сохранить громкость
//        }
//    }
//
//    // Ваш метод для прослушивания жизненного цикла приложения, где нужно восстановить громкость при выходе
//    // Например:
//    fun onAppExit() {
//        restoreOriginalVolume()
//    }
}
