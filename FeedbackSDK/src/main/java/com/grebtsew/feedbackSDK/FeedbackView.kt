package com.grebtsew.feedbackSDK

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StyleableRes
import androidx.core.graphics.ColorUtils

enum class Animations(val str: String) {
    SLIDE("SLIDE"),
    FLIP("FLIP"),
    FADE("FADE"),
    CUSTOM("CUSTOM")
}

class FeedbackView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var defaultColor = "#808080"
    private var startColor = "#FF0000"
    private var endColor = "#00FF00"

    var customAnimationFunction = { ownAnimationSample() } // If change enum is custom

    // Some example text for default case
    private var numberOfLevels = 4 // StateFunction must return int within number of Levels!
    private var levelTextArray = arrayOf(
            "Not initiated!",
            "Fill in more words!",
            "Just a bit more words!",
            "Perfect, your text is acceptable!"
    )

    private var levelAnimationArray = arrayOf(
            Animations.FLIP,
            Animations.SLIDE,
            Animations.FADE,
            Animations.CUSTOM
    )

    private var levelColorArray =
            arrayOf(defaultColor, startColor, "#FFFF00", endColor)

    private var currentLevel = 0

    var input = EditText(context)
    var feedback = TextView(context)

    var FEEDBACK_ANALYZE_FUNC = { str: String -> wordSizeLimitSample(str) }

    private fun ownAnimationSample() {
        /*
        * NOTE: animate the "feedback" public component!
        * */

        val bounce = TranslateAnimation(
                0f,  // fromXDelta
                0f,  // toXDelta
                0f,  // fromYDelta
                -100f
        )
        bounce.repeatMode = Animation.REVERSE
        bounce.duration = (1000..2000).random().toLong()
        feedback.startAnimation(bounce)
    }

    private fun wordSizeLimitSample(str: String): Int {
        // A sample text analysis function for default usages!
        // This function calculates which feedback state we are in.
        // NOTE: return values must be lower than numberOfLevels variable!
        val length = str.length
        if (length == 0) {
            return 0
        }
        if (length < 4) {
            return 1
        }
        if (length < 10) {
            return 2
        } else {
            return 3
        }
    }

    fun setFeedbackData(func: (String) -> Int, _LevelTextArray: Array<String>) {
        // Use this if you want to initiate obligatory data fields!
        FEEDBACK_ANALYZE_FUNC = func
        levelTextArray = _LevelTextArray
        numberOfLevels = levelTextArray.size

        if (numberOfLevels > levelAnimationArray.size) {
            for (i in IntRange(0, levelAnimationArray.size - numberOfLevels)) {
                levelAnimationArray += Animations.FLIP
            }
        }

        if (numberOfLevels >= levelColorArray.size) {
            for (i in IntRange(0, levelColorArray.size - numberOfLevels)) {
                levelColorArray += endColor
            }
        } else {
            calculateColor()
        }
    }

    fun setAnimationArray(_LevelAnimationArray: Array<Animations>?) {
        // Use this if you want to customize animations
        if (_LevelAnimationArray != null) {
            levelAnimationArray = _LevelAnimationArray
        }
    }

    fun setColors(_default: String?, _start: String?, _end: String?) {
        if (_default != null) {
              defaultColor = _default
        }
        if (_start != null) {
            startColor = _start
        }
        if (_end != null) {
            endColor = _end
        }
        calculateColor()
    }

    private fun calculateColor() {
        // Calculate all colors from specified colors!
        levelColorArray = arrayOf()

        levelColorArray += defaultColor

        val highest_level = numberOfLevels - 1
        for (i in IntRange(1, highest_level)) {
            levelColorArray += "#" + java.lang.Integer.toHexString(
                    ColorUtils.blendARGB(
                            Color.parseColor(
                                    startColor
                            ), Color.parseColor(endColor), i.toFloat() / highest_level.toFloat()
                    )
            ).substring(2)
        }
    }

    private fun update(level: Int) {
        updateText(level)
        updateAnimation(level)
        updateColor(level)
    }

    private fun updateColor(level: Int) {
        feedback.setBackgroundColor(Color.parseColor(levelColorArray[level]))
    }

    private fun updateText(level: Int) {
        feedback.text = levelTextArray[level]
        numberOfLevels = levelTextArray.size
    }

    private fun updateAnimation(level: Int) {
        when (levelAnimationArray[level]) {

            Animations.FLIP -> {
                val oa1 = ObjectAnimator.ofFloat(feedback, "scaleX", 1f, 0f)
                val oa2 = ObjectAnimator.ofFloat(feedback, "scaleX", 0f, 1f)
                oa1.interpolator = DecelerateInterpolator()
                oa2.interpolator = AccelerateDecelerateInterpolator()
                oa1.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        oa2.start()
                    }
                })
                oa1.start()
            }

            Animations.SLIDE -> {
                val animate = TranslateAnimation(
                        0f,  // fromXDelta
                        0f,  // toXDelta
                        200f,  // fromYDelta
                        0f
                ) // toYDelta

                animate.duration = 500
                animate.fillAfter = true
                feedback.startAnimation(animate)
            }
            Animations.FADE -> {
                val fadeOut: Animation = AlphaAnimation(1f, 0f)
                fadeOut.interpolator = AccelerateInterpolator() //and this
                fadeOut.startOffset = 1000
                fadeOut.duration = 1000
                feedback.startAnimation(fadeOut)

                val fadeIn: Animation = AlphaAnimation(0f, 1f)
                fadeIn.interpolator = DecelerateInterpolator() //add this
                fadeIn.duration = 1000
                feedback.startAnimation(fadeIn)
            }
            Animations.CUSTOM -> {
                customAnimationFunction()
            }
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.feedbackview, this, true)

        input = findViewById<EditText>(R.id.input)
        feedback = findViewById<TextView>(R.id.feedback)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.FeedbackView)

        @StyleableRes val index0 = 0
        @StyleableRes val index1 = 1
        @StyleableRes val index2 = 2

        val _colorStart = typedArray.getString(index0)
        val _colorEnd = typedArray.getString(index1)
        val _defaultColor = typedArray.getString(index2)
        typedArray.recycle()

        setColors(_defaultColor, _colorStart, _colorEnd)

        calculateColor()

        update(0)

        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val statusLevel = FEEDBACK_ANALYZE_FUNC(input.text.toString())

                if (statusLevel != currentLevel) {
                    currentLevel = statusLevel
                    update(currentLevel)
                }
            }
        })
    }
}

