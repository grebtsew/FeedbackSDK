package com.example.mysdk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        /*

        -----  Ai image test code -----

        */

        val textIndex = findViewById<EditText>(R.id.editTextNumberSigned)
        val textField = findViewById<EditText>(R.id.editTextTextPersonName)
        val aiImage = findViewById<com.grebtsew.feedbackSDK.AiImage>(R.id.aiimage)
        val imageAmountTextView = findViewById<TextView>(R.id.textView5)

        aiImage.OnImageAmountChangelistener.observe(this, Observer {
            imageAmountTextView.text = it.toString()
        })

        aiImage.OnImageUpdatedlistener.observe(this, Observer {
            Log.i("AIIMAGE", "Image updated!")
        })

        textField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                aiImage.update(textField.text.toString());
            }
        })

        textIndex.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    aiImage.index = s.toString().toInt();
                    aiImage.update();
                }
            }
        })


        /*

        -----  Voice to Text button Test code -----

        */


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission()
        }

        val latestVoice = findViewById<TextView>(R.id.latestVoice)
        val allVoice = findViewById<TextView>(R.id.allVoice)
        val voiceToTextButton = findViewById<com.grebtsew.feedbackSDK.VoiceToTextButton>(R.id.voicetotextbutton)
        var all = ""

        voiceToTextButton.listen.observe(this, Observer {
            all += " " + it.toString()
            latestVoice.setText(it.toString())
            allVoice.setText(all)
        })

        /*

       -----  Feedback Test Code -----
        Nothing needed for demo!
       */


        /*

        ----- Integration Test Code -----

        */


        val integratedAiImage = findViewById<com.grebtsew.feedbackSDK.AiImage>(R.id.integratedAIimage)
        val integratedVoiceToTextButton = findViewById<com.grebtsew.feedbackSDK.VoiceToTextButton>(R.id.integratedVoiceToTextButton)
        val integratedFeedbackView = findViewById<com.grebtsew.feedbackSDK.FeedbackView>(R.id.integratedFeedbackView)
        var integratedTextEdit = integratedFeedbackView.input

        // Connect Feedback input with image
        integratedTextEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count > 0) {
                    integratedAiImage.update(integratedTextEdit.text.toString());
                }
            }
        })

        // Connect Speech to text to textview
        integratedVoiceToTextButton.listen.observe(this, Observer {
            integratedTextEdit.setText(integratedTextEdit.text.toString() + " " + it.toString());
            integratedAiImage.update(integratedTextEdit.text.toString());
        })
    }


    // Help functions for Speech to Text!

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.size > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }


}