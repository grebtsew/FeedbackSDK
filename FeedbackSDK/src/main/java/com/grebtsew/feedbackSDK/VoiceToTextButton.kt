package com.grebtsew.feedbackSDK

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.RelativeLayout
import androidx.annotation.StyleableRes
import androidx.lifecycle.MutableLiveData
import java.util.*


class VoiceToTextButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var text = ""
    private var isRunning = false
    private var button = Button(context)
    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    private val speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    val listen = MutableLiveData<String>()

    fun handleClick() {

        if (isRunning) {
            speechRecognizer.stopListening()
            button.text = "Press and speak!"
            isRunning = false
        } else {
            speechRecognizer.startListening(speechRecognizerIntent)
            isRunning = true
            button.text = "Listening... Press again to stop!"
        }
    }


    init {
        LayoutInflater.from(context).inflate(R.layout.voicetotextbutton, this, true)

        button = findViewById<Button>(R.id.button)

        isClickable = true
        isFocusable = true

        speechRecognizerIntent.putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        speechRecognizerIntent.putExtra("android.speech.extra.DICTATION_MODE", true)

        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.VoiceToTextButton)

        @StyleableRes val index0 = 0

        val language = typedArray.getString(index0)

        if (language != null) {
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language.toString())
            Log.i("VoiceToText", "Language: " + language.toString())
        } else {
            speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            Log.i("VoiceToText", "Language: " + Locale.getDefault())
        }

        // Use this to See if device can handle Speech recognition!
        Log.i(
                "VoiceToText",
                "Can use speech-to-text: " + SpeechRecognizer.isRecognitionAvailable(context).toString()
        )


        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(v: Float) {
            }

            override fun onBufferReceived(bytes: ByteArray) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(i: Int) {
                var mError = ""

                when (i) {
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                        mError = " network timeout"

                    }
                    SpeechRecognizer.ERROR_NETWORK -> {
                        mError = " network"
                        //toast("Please check data bundle or network settings");

                    }
                    SpeechRecognizer.ERROR_AUDIO -> mError = " audio"
                    SpeechRecognizer.ERROR_SERVER -> {
                        mError = " server"

                    }
                    SpeechRecognizer.ERROR_CLIENT -> mError = " client"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> mError = " speech time out"
                    SpeechRecognizer.ERROR_NO_MATCH -> {
                        mError = " no match"

                    }
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> mError = " recogniser busy"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> mError =
                            " insufficient permissions"
                }
                Log.i("VoiceToText", "Error: " + i.toString() + " - " + mError)

            }

            override fun onResults(bundle: Bundle) {
                val matches =
                        bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)//getting all the matches
                //displaying the first match
                if (matches != null) {
                    text = matches[0]
                    listen.value = text
                }
                handleClick()
            }

            override fun onPartialResults(bundle: Bundle) {
            }

            override fun onEvent(i: Int, bundle: Bundle) {
            }
        })

        button.setOnClickListener(OnClickListener {
            handleClick()
        })
    }

}