package com.example.learningwords

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity()/*, WordsFragmentListener */ {

    // Объявление переменных
    lateinit var adapterWords: ArrayAdapter<String> // адаптер для spinnerWords

    private var isProgrammaticPageChange = false  // Флаг для предотвращения рекурсии

    // Инициализация классов =======================================================================
    val data = DataManager() // мои данные флагов, счетчиков и переменных
    val textAnimator = TextAnimator(this) // анимация и текст с расширенной срединной точкой

    lateinit var init: UiInitializer
    lateinit var contr: UiController
    lateinit var app: AppController
    lateinit var sett: MySettings
    lateinit var volume: VolumeManager
    private lateinit var prefs: PreferencesManager

    var isCreate = false // показывает что была перезагрузка
    var flagIndexFragmentZero = false // флаг дает разрешение обнулить позицию фрагмента в итемах "Уровни" и "Перемешать"

    // onCreate =====================================================================================
    @SuppressLint("SuspiciousIndentation", "CutPasteId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {

        isCreate = true

        //--------------------
        // Инициализируем PreferencesManager с контекстом активити
        prefs = PreferencesManager(this, data)

        // Загружаем сохраненное состояние светлого/темного режима
        // Восстановление старой сессии из сохраненных данных
        prefs.loadState()

        // Применяем соответствующую тему
        if (data.isDarkMode_) {
            setTheme(R.style.AppTheme_Dark) // Имя тёмной темы из ресурсов тем
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Переключаемся на темную тему
        } else {
            setTheme(R.style.AppTheme) // Имя светлой темы из ресурсов тем
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Переключаемся на светлую тему
        }
        //---------------------

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init = UiInitializer(this) // Инициализация UI-компонентов
        init.initViews() // Инициализация остальных виджетов
        contr = UiController(this)
        contr.createMySettingsInstance()
        app = AppController(this)
        sett = MySettings(this)


        //--- Инициализация UI-компонентов =========================================================

        init.initViews() // инициализируем все виджеты
        if (!data.isStatistics_) {
            sett.statistics(View.GONE) // убираем с макета виджеты статистики
            init.pressStatistics.visibility = View.GONE // выключаем слушатель
        } else
            init.pressStatistics.visibility = View.VISIBLE // включаем слушатель
        if (!data.isIndicator_) sett.indicators(View.GONE) // убираем с макета виджеты индикаторов
        if (!data.isVolume_) sett.volume() // убираем с макета виджеты громкости
        if (data.isManual) sett.manual() // показываем текст инструкции


        //--- код для виджета громкости ============================================================

        volume = VolumeManager(this, init.volumeSeekBar, init, data)

        //---
        val keyboardInitializer = KeyboardInitializer() // Создаем экземпляр класса инициализатора и инициализируем TextViews
        data.listKeyboardTextView = keyboardInitializer.initLetterTextViews(this) // инициализируем список TextView букв

        // копирование в файл и считывание из него нового выбранного словаря
        if (data.dictionaryTripleList_ == null)
            app.loadDictionary() // загружаем информацию из файла
        else {
            init.textViewTotalAnswers.text = data.totalSize_.toString() // выводим на экран количество слов в словаре

            if (data.intWordListLevel_.size == 0)
                data.intWordListLevel_ = MutableList(data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0
        }

        //--- фрагмент и адаптер ===================================================================
        //--- инициализируйте ViewPager2 и установите для него адаптер

        app.updateFragmentWords() // Обновить список элементов во фрагменте англ. слов

        //--- Показываем фрагмент
        if (savedInstanceState == null) {
            WordsFragment()
        }

        //--- Адаптер и Фрагмент --- Установка слушателя для отслеживания текущей страницы
        init.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                // Определение следующей позиции
                if (!flagIndexFragmentZero) { // если запрос не из итемов по нажатиям "Уровни" и "Перемешать"
                    val nextPosition = if (data.isButton) {
                        data.isButton = false
                        data.currentIndex_
                    } else {
                        // Этот код выполнится при смене страницы 'position' содержит индекс текущей страницы в ViewPager
                        if (isProgrammaticPageChange) {
                            isProgrammaticPageChange = false  // Сбрасываем флаг и выходим
                            data.previousPosition = position  // Обновляем предыдущую позицию
                            return
                        }
                        val direction =
                            position - data.previousPosition  // Определяем направление свайпа
                        calculateNextPosition(position, direction)
                    }

                    data.previousPosition = position  // Обновляем предыдущую позицию

                    // Обновление UI при необходимости
                    if (nextPosition != data.previousPosition && !flagIndexFragmentZero) {
                        isProgrammaticPageChange = true // Взводим флаг перед изменением позиции
                        init.viewPager.setCurrentItem(nextPosition, false)  // Перемещаемся, выключаем анимацию
                    }
                }

                // Стандартные действия, выполняемые при смене страницы
                data.flagButtonSee = false // флаг отслеживает - если кнопка подсказка была нажата
                app.update() // по нажатии кнопок восстанавливаем все значения по умолчанию

                // проверяем условия для отключения спиннер
                contr.lockSpinnerWords() // отключаем спиннер слов

                if (flagIndexFragmentZero) { // если запрос из итемов по нажатиям "Уровни" и "Перемешать"
                    flagIndexFragmentZero = false

                    if (data.isIgnore_) { // определяем шаг (направление) перемещения фрагмента
                        if (data.intWordListLevel_[data.dictionaryTripleList_!![data.currentIndex_].first] != 1)
                            data.previousPosition = 0
                        else
                            data.previousPosition = 1
                    } else if (data.isHide_) {
                        if (data.intWordListLevel_[data.dictionaryTripleList_!![data.currentIndex_].first] == 0)
                            data.previousPosition = 0
                        else
                            data.previousPosition = 1
                    } else
                        data.previousPosition = 0

                    val index = calculateNextPosition(data.currentIndex_, data.previousPosition)
                    init.viewPager.setCurrentItem(index, false)
                }
            }
        })


        // клики ===================================================================================

        //--- нажатие кнопки "Вперед" click listener
        init.buttonNext.setOnClickListener {
            data.isButton = true // не позволяет viewPager дублировать fun calculateNextPosition()
            contr.buttonNext()
        }
        // Код, который выполнится при долгом нажатии
        init.buttonNext.setOnLongClickListener { // Установка слушателя на долгое нажатие
            data.switchNext_ = !data.switchNext_ // Переключаем значение
            sett.paintVectorButtons(init.wrappedDrawableNext, data.switchNext_) // перекрашиваем векторные кнопки
            if (data.switchNext_ && data.switchBack_) { // если обе иконки включены, передыдущею отключаем
                data.switchBack_ = false
                sett.paintVectorButtons(init.wrappedDrawableBack, data.switchBack_) // перекрашиваем векторные кнопки
            }
            true // Возвращаем true, чтобы сообщить системе, что событие долгого нажатия было обработано
        }

        //--- нажатие кнопки "Назад" click listener
        init.buttonBack.setOnClickListener {
            data.isButton = true // не позволяет viewPager дублировать fun calculateNextPosition()
            contr.buttonBack()
        }
        // Код, который выполнится при долгом нажатии
        init.buttonBack.setOnLongClickListener { // Установка слушателя на долгое нажатие
            data.switchBack_ = !data.switchBack_ // Переключаем значение
            sett.paintVectorButtons(init.wrappedDrawableBack, data.switchBack_) // перекрашиваем векторные кнопки
            if (data.switchNext_ && data.switchBack_) { // если обе иконки включены, передыдущею отключаем
                data.switchNext_ = false
                sett.paintVectorButtons(init.wrappedDrawableNext, data.switchNext_) // перекрашиваем векторные кнопки
            }
            true // Возвращаем true, чтобы сообщить системе, что событие долгого нажатия было обработано
        }

        //--- Озвучить англ. слово
        init.buttonSpeak.setOnClickListener {
            init.textSpeaker.speak(init.textViewEn.text.toString())
        }
        // Код, который выполнится при долгом нажатии
        init.buttonSpeak.setOnLongClickListener { // Установка слушателя на долгое нажатие
            // перекрашиваем векторные кнопки
            data.switchSpeak_ = !data.switchSpeak_ // Переключаем значение
            sett.paintVectorButtons(init.wrappedDrawableSpeak, data.switchSpeak_) // перекрашиваем векторные кнопки
            if (data.switchSpeak_)
                init.textSpeaker.speak(init.textViewEn.text.toString())
            true // Возвращаем true, чтобы сообщить системе, что событие долгого нажатия было обработано
        }

        //--- посмотреть перевод без угадывания
        init.buttonEye.setOnClickListener {
            contr.buttonEye()
        }
        // Код, который выполнится при долгом нажатии
        init.buttonEye.setOnLongClickListener { // Установка слушателя на долгое нажатие
            // перекрашиваем векторные кнопки
            data.switchEye_ = !data.switchEye_ // Переключаем значение
            sett.paintVectorButtons(init.wrappedDrawableEye, data.switchEye_) // перекрашиваем векторные кнопки
            contr.buttonEye()
            true // Возвращаем true, чтобы сообщить системе, что событие долгого нажатия было обработано
        }

        //--- нажатие на месте кнопки громкости, когда она скрыта
        init.pressVolume.setOnClickListener {
            // код для виджета громкости
            volume.showSeekBarWithAnimation()
        }

        //--- нажатие на месте виджетов статистики, для их раскрытия (визуализации)
        init.pressStatistics.setOnClickListener {
            // показываем статистику
            if (data.isStatistics_)
                sett.statistics(View.VISIBLE)
        }

        //--- клик букв клавиатуры TextViews
        data.listKeyboardTextView.forEach { textView -> textView.setOnClickListener { v -> contr.onLetterClicked(v) } }

        //--- слушаем когда текст в textViewEn изменится
        init.textViewEn.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Этот метод вызывается, чтобы уведомить вас о том, что в нашем EditText вот-вот произойдут изменения.
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // Этот метод вызывается, чтобы уведомить вас о том, что в нашем EditText только что произошли изменения.
                init.spinnerWords.isEnabled = true // после перелистывания разблокируем спиннер
            }
            override fun afterTextChanged(s: Editable) {
                // Этот метод вызывается, чтобы уведомить вас о том, что после всех уведомлений
                // предыдущей onTextChanged последовательности родные методы ввода запрошены,
                // чтобы применить свои изменения, и текстовый элемент 'Editable' уже обновлен.
            }
        })


        //--- спиннер ==============================================================================

        //--- спиннер для выбора слова
        adapterWords = SpinnerManager(this, R.layout.custom_words_spinner, data.wordsListSpinner, data, init, { position ->
            if (position > 0) { // Логика выбора слова
                init.spinnerWords.isEnabled = false // после выбора варианта отключаем спиннер
            } })
        setupSpinnerWords(this, init.spinnerWords, adapterWords as SpinnerManager, data, init, contr, app)


        //--- создаем выдвижную панель Navigation Drawer ===========================================

        // контейнер для навигационной панели
        val toggle = ActionBarDrawerToggle(
            this,
            init.drawerLayout,
            init.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close)

        // Окрашивание бургера
        val arrowDrawable = toggle.drawerArrowDrawable
        arrowDrawable.color = ContextCompat.getColor(this, R.color.text_color)

        init.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // клики
        //--- навигационная панель
        // Уровни
        init.headerView.findViewById<TextView>(R.id.item_2).setOnClickListener {
            // Реакция на клик по "Уровни"
            // сохраняем из наших списков в память телефона
            prefs.saveIntList() //////////////////////////////////////////////////////////////////////////////////////
            // Пользователь подтвердил свой выбор уровня и сброса статистики
            val message = "Выберите уровень для доступа к соответствующему словарю."
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
            flagIndexFragmentZero = true
            sett.showDialogListLevels(prefs) // вызываем диалог для выбора уровня
        }
        // Голоса
        init.headerView.findViewById<TextView>(R.id.item_3).setOnClickListener {
            // Реакция на клик по "Голоса"
            sett.showDialogListSpeak()
        }
        // Показать
        init.headerView.findViewById<TextView>(R.id.item_4).setOnClickListener {
            // Реакция на клик по "Показать"
            data.isVision_ = !data.isVision_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon4, data.isVision_) // перекрашиваем векторные imageText
            if (data.isVision_) { // если иконка включена, предыдущие отключаем
                if (data.isIgnore_) {
                    data.isIgnore_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon5, false) // перекрашиваем векторные imageText
                }
                if (data.isHide_) {
                    data.isHide_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon6, false) // перекрашиваем векторные imageText
                }
            }
            val message = if (data.isVision_) {
                "Значения переведенных слов теперь отображаются."
            } else {
                "Все слова отображаются."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
        }
        // Пропустить
        init.headerView.findViewById<TextView>(R.id.item_5).setOnClickListener {
            // Реакция на клик по "Пропустить"
            data.isIgnore_ = !data.isIgnore_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon5, data.isIgnore_) // перекрашиваем векторные imageText
            if (data.isIgnore_) { // если иконка включена, предыдущие отключаем
                if (data.isVision_) {
                    data.isVision_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon4, false) // перекрашиваем векторные imageText
                }
                if (data.isHide_) {
                    data.isHide_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon6, false) // перекрашиваем векторные imageText
                }
            }
            val message = if (data.isIgnore_) {
                "Переведённые слова будут скрыты."
            } else {
                "Все слова отображаются."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
        }
        // Скрыть
        init.headerView.findViewById<TextView>(R.id.item_6).setOnClickListener {
            // Реакция на клик по "Скрыть"
            data.isHide_ = !data.isHide_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon6, data.isHide_) // перекрашиваем векторные imageText
            if (data.isHide_) { // если иконка включена, предыдущие отключаем
                if (data.isVision_) {
                    data.isVision_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon4, false) // перекрашиваем векторные imageText
                }
                if (data.isIgnore_) {
                    data.isIgnore_ = false
                    sett.paintVectorIcon(init.imageViewHeaderIcon5, false) // перекрашиваем векторные imageText
                }
            }
            val message = if (data.isHide_) {
                "Переведённые и ошибочно переведенные слова скрыты."
            } else {
                "Все слова отображаются."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
        }
        // Перемешать
        init.headerView.findViewById<TextView>(R.id.item_7).setOnClickListener {
            // Реакция на клик по "Перемешать"

            // Пользователь подтвердил свой выбор и готов к началу перемешивания слов и сбросу статистики
            data.isShuffled_ = !data.isShuffled_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon7, data.isShuffled_) // перекрашиваем векторные imageText
            val message = if (data.isShuffled_) {
                "Слова перемешаны."
            } else {
                "Слова восстановлены по порядку."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение

            flagIndexFragmentZero = true
            app.loadDictionary() // загружаем информацию из файла
            sett.setupLevel() // обнуляем и перекрашиваем счетчики слов

            if (data.intWordListLevel_.all { it == 0 }) { // если запрос из итемов по нажатиям "Перемешать", все позиции обнуляются
                data.currentIndex_ = 0
                data.previousPosition = 0
                val index = calculateNextPosition(data.currentIndex_, data.previousPosition)
                init.viewPager.setCurrentItem(index, false)
            }
        }
        // Статистика
        init.headerView.findViewById<TextView>(R.id.item_8).setOnClickListener {
            // Реакция на клик по "Статистика"
            data.isStatistics_ = !data.isStatistics_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon8, data.isStatistics_) // перекрашиваем векторные imageText
            val message = if (data.isStatistics_) {
                sett.statistics(View.VISIBLE) // Вызов функции виджета статистики
                init.pressStatistics.visibility = View.VISIBLE // включаем слушатель
                "Статистика отображается."
            } else {
                sett.statistics(View.GONE) // Вызов функции виджета статистики
                init.pressStatistics.visibility = View.GONE // выключаем слушатель
                "Статистика скрыта."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
            //-------------
//            Snackbar.make(headerView, message, Snackbar.LENGTH_LONG).setAction("Отмена") {
//                    // Код на действие по отмене
//                }.show()
            //------------
//            sett.statistics() // Вызов функции виджета статистики
        }
        // Индикаторы
        init.headerView.findViewById<TextView>(R.id.item_9).setOnClickListener {
            // Реакция на клик по "Индикаторы"
            data.isIndicator_ = !data.isIndicator_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon9, data.isIndicator_) // перекрашиваем векторные imageText
            val message = if (data.isIndicator_) {
                sett.indicators(View.VISIBLE) // Вызов функции виджета индикаторов
                "Индикаторы символов включены."
            } else {
                sett.indicators(View.GONE) // Вызов функции виджета индикаторов
                "Индикаторы символов скрыты."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
//            sett.indicators() // Вызов функции виджета индикаторов
        }
        // Громкость
        init.headerView.findViewById<TextView>(R.id.item_10).setOnClickListener {
            // Реакция на клик по "Громкость"
            data.isVolume_ = !data.isVolume_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon10, data.isVolume_) // перекрашиваем векторные imageText
            val message = if (data.isVolume_) {
                "Виджет громкости отображается."
            } else {
                "Виджет громкости скрыт."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение

            if (data.isVolume_) // показываем на экране виджет громкости
                volume.showSeekBarWithAnimation()

            sett.volume() // Вызов функции виджета громкости
        }
        // Темная тема
        init.headerView.findViewById<TextView>(R.id.item_11).setOnClickListener {
            // Реакция на клик по "Темная тема"
            data.isDarkMode_ = !data.isDarkMode_ // Переключаем значение
            sett.paintVectorIcon(init.imageViewHeaderIcon11, data.isDarkMode_) // перекрашиваем векторные imageText
            // Сохраняем состояние темного режима в SharedPreferences или в другом месте
            if (data.isDarkMode_) {
                // Переключаемся на темную тему
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // Переключаемся на светлую тему
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
//            recreate() // Пересоздаём активность с новой темой
            val message = if (data.isDarkMode_) {
                "Активирован темный режим с темным фоном и светлым текстом."
            } else {
                "Активирован светлый режим с светлым фоном и темным текстом."
            }
            Snackbar.make(init.headerView, message, Snackbar.LENGTH_LONG).show() // выводим сообщение
        }
        // Инструкция
        init.headerView.findViewById<TextView>(R.id.item_12).setOnClickListener {
            // Реакция на клик по "Инструкция"
            data.isManual = !data.isManual // Переключаем значение
            sett.manual() // Вызов функции инструкции

            //--- ставим стрелку вместо бургера ====================================================

            if (init.drawerLayout.isDrawerOpen(GravityCompat.START)) // если панель drawerLayout открыта
                init.drawerLayout.closeDrawer(GravityCompat.START) // закрываем drawerLayout

            // Если должны отображать стрелку возврата
            if (data.isManual) {
                toggle.isDrawerIndicatorEnabled = false // Отключаем иконку "бургера"
//                supportActionBar?.setDisplayHomeAsUpEnabled(true) // Включаем стрелку возврата

                val customBackArrow = ContextCompat.getDrawable(this, R.drawable.outline_arrow_back_24)
                init.toolbar.navigationIcon = customBackArrow // Установите свою стрелку для Toolbar
                // Примените тинт, если нужно изменить цвет
                customBackArrow?.setColorFilter(ContextCompat.getColor(this, R.color.text_color), PorterDuff.Mode.SRC_ATOP)
            }

            // Отменяем слушатель DrawerToggle и устанавливаем свой, который будет вызывать onBackPressed или вашу логику возврата
            toggle.setToolbarNavigationClickListener {
                // Реакция на клик по "Стрелка"
                data.isManual = !data.isManual // Переключаем значение
                sett.manual() // Вызов функции инструкции

                // возвращаем всё как было
//                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toggle.isDrawerIndicatorEnabled = true
                toggle.setToolbarNavigationClickListener(null)

                init.drawerLayout.openDrawer(GravityCompat.START) // открываем панель drawerLayout
            }
        }

        sett.paintIcons(this, init, data) // перекрашиваем все иконки по перезагрузке или смены темы
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    // функции =====================================================================================

    //--- Обновить список элементов в спиннере слов и фрагменте
    fun updateSpinnerAndFragmentWords() {
        data.wordsListSpinner = mutableListOf(data.variantsWord) // Изменяемый список
        data.wordsListSpinner.addAll(app.randomListRu()) // создает список из десяти случайных ру слов для спиннера
        if (this::adapterWords.isInitialized) { // создаем список в адаптере фрагмента
            adapterWords.clear() // Очищаем текущие данные
            adapterWords.addAll(data.wordsListSpinner) // Добавляем все элементы в адаптер
            adapterWords.notifyDataSetChanged() // Уведомляем адаптер о новом списке
            init.spinnerWords.setSelection(0) // Сбрасываем выбранный элемент и показываем первый элемент в списке
        }
    }

    // здесь пишем условие для свайпа с шагом для фрагмента и кнопок вперед/назад
    fun calculateNextPosition(position: Int, direction: Int): Int {

        contr.lettersPartialPress() // проверяем были ли частично нажаты буквы

        val listSize = data.dictionaryTripleList_?.size ?: 1

        // Инкремент или декремент текущего индекса в зависимости от направления
        fun incrementIndex() {
            val dir = when {
                direction > 0 -> 1
                direction < 0 -> -1
                else -> 0
            }
            data.currentIndex_ = (data.currentIndex_ + dir).let {
                if (it < 0) listSize - 1 else it % listSize
            }
        }

        // Проверка, нужно ли учитывать слово в текущем индексе
        fun isIndexApplicable(): Boolean {
//            return when {
//                data.isIgnore -> data.intWordListLevel[data.dictionaryTripleList!![data.currentIndex].first] != 1
//                data.isHide -> data.intWordListLevel[data.dictionaryTripleList!![data.currentIndex].first] == 0
//                else -> true
//            }

            return if (data.dictionaryTripleList_?.size == data.intWordListLevel_.size) { // проверка если размера библиотеки изменился, проверка для обновления версий
                when {
                    data.isIgnore_ -> data.intWordListLevel_[data.dictionaryTripleList_!![data.currentIndex_].first] != 1
                    data.isHide_ -> data.intWordListLevel_[data.dictionaryTripleList_!![data.currentIndex_].first] == 0
                    else -> true
                }
            } else {
                data.intWordListLevel_ = MutableList(data.dictionaryTripleList_!!.size) { 0 } // присвоим все 0
                true
            }
        }

        // Определяем, нужно ли фильтровать список (чтобы исключить скрываемые слова, и если да, находим следующий индекс
        val shouldFilterList = when {
            data.isIgnore_ -> data.intWordListLevel_.any { it != 1 }
            data.isHide_ -> data.intWordListLevel_.any { it == 0 }
            else -> false
        }

        if (shouldFilterList) {
            do { incrementIndex() } while (!isIndexApplicable())
        } else if (!data.isIgnore_ && !data.isHide_) {
            incrementIndex()
        } else if (data.isIgnore_) {
            data.intWordListLevel_.forEachIndexed { index, value -> if (value == 1) data.intWordListLevel_[index] = 0 }
            // тост - все слова угаданы и стали видимы, открыт доступ на следующий уровень
        } else if (data.isHide_) {
            if (data.intWordListLevel_.any { it == -1 })
                data.intWordListLevel_.forEachIndexed { index, value -> if (value == -1) data.intWordListLevel_[index] = 0 }
                // тост - все не угаданные слова стали видимы
            else if (data.intWordListLevel_.any { it == 1 })
                data.intWordListLevel_.forEachIndexed { index, value -> if (value == 1) data.intWordListLevel_[index] = 0 }
                // тост - все слова угаданы и стали видимы, открыт доступ на следующий уровень
        }

        if (data.isVision_) {
            if (data.intWordListLevel_.all { it == 1 }) {
                data.intWordListLevel_.forEachIndexed { index, value ->
                    if (value == 1) data.intWordListLevel_[index] = 0
                }
                // тост - все слова угаданы и стали видимы, открыт доступ на следующий уровень
            }
        }

        return data.currentIndex_
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        // Сохранение данных для восстановления в новой сессии
        prefs.saveState()

    }

    override fun onDestroy() {
        super.onDestroy()
        init.textSpeaker.shutdown()
        // Останавливаем автовоспроизведение, если пользователь покидает Activity
        contr.stopAutoScrolling()
    }
}