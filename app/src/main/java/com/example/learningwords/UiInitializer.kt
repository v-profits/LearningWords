package com.example.learningwords

import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView

// Инициализация UI-компонентов
class UiInitializer(val activity: MainActivity) {

    // Объявление переменных

    lateinit var constraintLayout: ConstraintLayout
    lateinit var volumeSeekBar: VerticalSeekBar
    lateinit var drawerLayout: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: Toolbar // Установка кастомного тулбара
    lateinit var viewPager: ViewPager2
    lateinit var wordsPagerAdapter: WordsPagerAdapter
    lateinit var gridLayout: GridLayout

    lateinit var textViewRu: TextView
    lateinit var textViewEn: TextView
    lateinit var textViewCorrPercen: TextView
    lateinit var textViewErrPercen: TextView
    lateinit var buttonNext: ImageButton
    lateinit var buttonBack: ImageButton
    lateinit var textSpeaker: TextSpeaker
    lateinit var buttonSpeak: ImageButton
    lateinit var buttonEye: ImageButton
    lateinit var pressVolume: TextView
    lateinit var pressStatistics: TextView
    lateinit var spinnerWords: Spinner
    lateinit var corrProgressBar: CorrectIndicatorView // объект класса CorrectIndicatorView
    lateinit var errProgressBar: ErrorIndicatorView // объект класса ErrorIndicatorView
    lateinit var textViewTotalAnswers: TextView
    lateinit var textViewCurrentAnswers: TextView
    lateinit var textViewCorrectAnswers: TextView
    lateinit var textViewWrongAnswers: TextView
    lateinit var textViewManual: TextView

    // виджиты для скрытия статистики
    lateinit var wordTextIndCorr: TextView
    lateinit var wordTextIndErr: TextView
    lateinit var textCorrPercen: TextView
    lateinit var textErrPercen: TextView

    // итемы в видвижной панели меню
    lateinit var headerView: View
    lateinit var imageViewHeaderIcon2: ImageView
    lateinit var imageViewHeaderIcon3: ImageView
    lateinit var imageViewHeaderIcon4: ImageView
    lateinit var imageViewHeaderIcon5: ImageView
    lateinit var imageViewHeaderIcon6: ImageView
    lateinit var imageViewHeaderIcon7: ImageView
    lateinit var imageViewHeaderIcon8: ImageView
    lateinit var imageViewHeaderIcon9: ImageView
    lateinit var imageViewHeaderIcon10: ImageView
    lateinit var imageViewHeaderIcon11: ImageView
    lateinit var imageViewHeaderIcon12: ImageView

    lateinit var tvNavHeader2: TextView // тексты в меню
    lateinit var tvNavHeader3: TextView
    lateinit var tvNavHeader4: TextView
    lateinit var tvNavHeader5: TextView
    lateinit var tvNavHeader6: TextView
    lateinit var tvNavHeader7: TextView
    lateinit var tvNavHeader8: TextView
    lateinit var tvNavHeader9: TextView
    lateinit var tvNavHeader10: TextView
    lateinit var tvNavHeader11: TextView
    lateinit var tvNavHeader12: TextView


    //--- инициализируем значки drawable для смены цвета ================================================================
    // Получаем Drawable ресурс
    val drawableNext = ContextCompat.getDrawable(activity, R.drawable.outline_arrow_forward_ios_24)?.mutate()
    val drawableBack = ContextCompat.getDrawable(activity, R.drawable.outline_arrow_back_ios_24)?.mutate()
    val drawableEye = ContextCompat.getDrawable(activity, R.drawable.outline_translate_24)?.mutate()
    val drawableSpeak = ContextCompat.getDrawable(activity, R.drawable.outline_graphic_eq_24)?.mutate()
    val drawableVolume = ContextCompat.getDrawable(activity, R.drawable.outline_volume_up_24)?.mutate()

    // Оборачиваем наш Drawable для совместимости
    val wrappedDrawableNext = drawableNext?.let { DrawableCompat.wrap(it) }
    val wrappedDrawableBack = drawableBack?.let { DrawableCompat.wrap(it) }
    val wrappedDrawableEye = drawableEye?.let { DrawableCompat.wrap(it) }
    val wrappedDrawableSpeak = drawableSpeak?.let { DrawableCompat.wrap(it) }
    val wrappedDrawableValume = drawableVolume?.let { DrawableCompat.wrap(it) }

    fun initViews() {
        with(activity) {

            //--- инициализация ===============================================================

            constraintLayout = findViewById(R.id.main)
            volumeSeekBar = findViewById(R.id.volumeSeekBar)
            textViewRu = findViewById(R.id.textViewRu)
            textViewEn = findViewById(R.id.textViewEn)
            textViewCorrPercen = findViewById(R.id.textViewCorPercen)
            textViewErrPercen = findViewById(R.id.textViewErrPercen)
            buttonNext = findViewById(R.id.buttonNext)
            buttonBack = findViewById(R.id.buttonBack)
            gridLayout = findViewById(R.id.gridLayout)
            textSpeaker = TextSpeaker(this, data)
            pressVolume = findViewById(R.id.pressVolume)
            pressStatistics = findViewById(R.id.pressStatistics)
            buttonSpeak = findViewById(R.id.buttonSpeak)
            buttonEye = findViewById(R.id.buttonEye)


            spinnerWords = findViewById(R.id.spinnerWords)

            corrProgressBar = findViewById(R.id.correctProgressBar)
            errProgressBar = findViewById(R.id.errorProgressBar)

            viewPager = findViewById(R.id.viewPager)
            textViewTotalAnswers = findViewById(R.id.textViewTotalAnswers)
            textViewCurrentAnswers = findViewById(R.id.textViewCurrentAnswers)
            textViewCorrectAnswers = findViewById(R.id.textViewCorrectAnswers)
            textViewWrongAnswers = findViewById(R.id.textViewWrongAnswers)

            textViewManual = findViewById(R.id.textViewManual)
            textViewManual.movementMethod = ScrollingMovementMethod()
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val versionName = packageInfo.versionName
            val manualText = String.format(getString(R.string.manual), versionName)
            textViewManual.text = Html.fromHtml(manualText, Html.FROM_HTML_MODE_COMPACT)
//            textViewManual.text = Html.fromHtml(getString(R.string.manual), Html.FROM_HTML_MODE_COMPACT)
//            textViewManual.text = HtmlCompat.fromHtml(getString(R.string.manual), HtmlCompat.FROM_HTML_MODE_LEGACY)
//            textViewManual.text = getString(R.string.manual2)

            // виджиты для скрытия статистики
            wordTextIndCorr = findViewById(R.id.wordTextIndCor)
            wordTextIndErr = findViewById(R.id.wordTextIndErr)
            textCorrPercen = findViewById(R.id.textCorPercen)
            textErrPercen = findViewById(R.id.textErrPercen)

            // контейнер для навигационной панели
            drawerLayout = findViewById(R.id.drawer_layout) // контейнер для навигационной панели

            // навигационная панель
            navigationView = findViewById(R.id.nav_view) // навигационная панель
            // итемы в видвижной панели меню
            headerView = init.navigationView.getHeaderView(0) // 0, если у вас один header

            imageViewHeaderIcon2 = headerView.findViewById(R.id.imageViewHeaderIcon_2)
            imageViewHeaderIcon3 = headerView.findViewById(R.id.imageViewHeaderIcon_3)
            imageViewHeaderIcon4 = headerView.findViewById(R.id.imageViewHeaderIcon_4)
            imageViewHeaderIcon5 = headerView.findViewById(R.id.imageViewHeaderIcon_5)
            imageViewHeaderIcon6 = headerView.findViewById(R.id.imageViewHeaderIcon_6)
            imageViewHeaderIcon7 = headerView.findViewById(R.id.imageViewHeaderIcon_7)
            imageViewHeaderIcon8 = headerView.findViewById(R.id.imageViewHeaderIcon_8)
            imageViewHeaderIcon9 = headerView.findViewById(R.id.imageViewHeaderIcon_9)
            imageViewHeaderIcon10 = headerView.findViewById(R.id.imageViewHeaderIcon_10)
            imageViewHeaderIcon11 = headerView.findViewById(R.id.imageViewHeaderIcon_11)
            imageViewHeaderIcon12 = headerView.findViewById(R.id.imageViewHeaderIcon_12)

            tvNavHeader2 = headerView.findViewById(R.id.tvNavHeader_2) // тексты в меню
            tvNavHeader3 = headerView.findViewById(R.id.tvNavHeader_3)
            tvNavHeader4 = headerView.findViewById(R.id.tvNavHeader_4)
            tvNavHeader5 = headerView.findViewById(R.id.tvNavHeader_5)
            tvNavHeader6 = headerView.findViewById(R.id.tvNavHeader_6)
            tvNavHeader7 = headerView.findViewById(R.id.tvNavHeader_7)
            tvNavHeader8 = headerView.findViewById(R.id.tvNavHeader_8)
            tvNavHeader9 = headerView.findViewById(R.id.tvNavHeader_9)
            tvNavHeader10 = headerView.findViewById(R.id.tvNavHeader_10)
            tvNavHeader11 = headerView.findViewById(R.id.tvNavHeader_11)
            tvNavHeader11 = headerView.findViewById(R.id.tvNavHeader_12)

            //--- создаем выдвижную панель Navigation Drawer ====================================

            toolbar = findViewById(R.id.toolbar) // toolbar - Установка кастомного тулбара
            supportActionBar?.title = "" // изменим заголовок Toolbar
            setSupportActionBar(toolbar)


            //--- инициализируем значки drawable для смены цвета ================================================================

            // Задаем новый цвет, который мы хотим использовать
            val colorNext = ContextCompat.getColor(this, R.color.text_color)
            wrappedDrawableNext?.let { DrawableCompat.setTint(it, colorNext) }
            val colorBack = ContextCompat.getColor(this, R.color.text_color)
            wrappedDrawableBack?.let { DrawableCompat.setTint(it, colorBack) }
            val colorEye = ContextCompat.getColor(this, R.color.text_color)
            wrappedDrawableEye?.let { DrawableCompat.setTint(it, colorEye) }
            val colorSpeak = ContextCompat.getColor(this, R.color.text_color)
            wrappedDrawableSpeak?.let { DrawableCompat.setTint(it, colorSpeak) }

            // Устанавливаем Drawable как изображение view. Например, установить на ImageView
            init.buttonNext.setImageDrawable(wrappedDrawableNext)
            init.buttonBack.setImageDrawable(wrappedDrawableBack)
            init.buttonEye.setImageDrawable(wrappedDrawableEye)
            buttonSpeak.setImageDrawable(wrappedDrawableSpeak)
        }
    }
}