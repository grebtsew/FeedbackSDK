package com.grebtsew.feedbackSDK

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.StyleableRes
import androidx.lifecycle.MutableLiveData
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import org.json.JSONObject


class AiImage : LinearLayout {
    @JvmOverloads
    constructor(
            context: Context,
            attrs: AttributeSet? = null,
            defStyleAttr: Int = 0
    )
            : super(context, attrs, defStyleAttr) {
        initiate(context, attrs)
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    )
            : super(context, attrs, defStyleAttr, defStyleRes) {
        initiate(context, attrs)
    }

    private val queue = Volley.newRequestQueue(context)
    private var imageView = ImageView(context);
    private val restApiHandler = RestApiHandler();

    var restApiHandlerInterface = object : RestApiHandlerInterface {
        override fun setRestApiUrl(defaulturl: String): String {
            return restApiHandler.setRestApiUrl(defaulturl)
        }

        override fun parseResponseToUrl(json: JSONObject, index: Int): Pair<String, Int>? {
            return restApiHandler.parseResponseToUrl(json, index)
        }
    }

    var text = ""
    var index = 0

    private val ERROR_LOG_TAG = "ERROR_AI_IMAGE"
    var DEFAULT_IMAGE =
            "https://www.dalalstreetwinners.com/wp-content/uploads/2019/09/Option-strategy-Example.jpg"

    private val SPLASHBASE_BASE_URL = "http://www.splashbase.co/api/v1/images/search?query="
    private var BASE_URL = SPLASHBASE_BASE_URL
    private var imageAmount = 0 // Important for reference usage purposes!

    val OnImageAmountChangelistener = MutableLiveData<Int>()

    val OnImageUpdatedlistener = MutableLiveData<String>()

    fun getImageView(): ImageView {
        return imageView;
    }

    fun getImageAmount(): Int {
        return imageAmount;
    }

    fun getSignificantWords(_text: String, amountOfWords: Int = 3): String {
        /**
         * Simpleton NLP function, collecting most significant words from a text
         * */

        var amountOfWordsToAdd = amountOfWords
        val wordArray = _text.split(" ").toTypedArray()

        if (wordArray.size == 0) {
            return ""
        }

        if (wordArray.size < amountOfWordsToAdd) {
            amountOfWordsToAdd = wordArray.size
        }

        val words = wordArray.filter { x: String? -> x != "" }
                .groupingBy { it }
                .eachCount()
                .toList()
                .sortedByDescending { it.second }
                .take(amountOfWordsToAdd)

        // Generate string from array of words
        var res = ""
        words.forEach {
            res += " " + it.first
        }

        return res
    }

    private fun sendRequest(url: String) {

        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->

                    val rootObject = JSONObject(response)
                    updateImage(rootObject)
                },
                Response.ErrorListener { response ->
                    Log.e(ERROR_LOG_TAG, response.toString());
                })

        queue.add(stringRequest)

    }

    private fun getAndUpdateImage(word: String) {
        /** Collects json data for search word */
        sendRequest(BASE_URL + word.replace(" ", "_"))
    }

    private fun updateImage(json: JSONObject) {

        val urlAmountPair = restApiHandlerInterface.parseResponseToUrl(json, index);

        if (urlAmountPair == null) {
            Log.e(ERROR_LOG_TAG, "No url found after request and parse functions!")
            if (imageAmount != 0) {
                imageAmount = 0
                OnImageAmountChangelistener.value = imageAmount;
            }
        } else {
            val imageUrl = urlAmountPair.first;

            if (imageAmount != urlAmountPair.second) {
                imageAmount = urlAmountPair.second;
                OnImageAmountChangelistener.value = imageAmount;
            }

            if (imageUrl != "") {
                loadImage(imageUrl)
            }
        }
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this).load(imageUrl).into(imageView)
        OnImageUpdatedlistener.value = imageUrl
    }

    fun update(_text: String = "") {
        /**
         * This is our update trigger function, supposed to be used whenever image is to be updated!
         * */

        if (_text != "") {
            text = _text
        }
        // No text entered!
        if (text == "") {
            return
        }

        val wordToQuery = getSignificantWords(text)
        getAndUpdateImage(wordToQuery)
    }

    private fun initiate(
            context: Context,
            attrs: AttributeSet?
    ) {

        // get the inflater service from the android system
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        // inflate the layout into "this" component
        inflater.inflate(R.layout.aiimage, this)

        // Initiate Values and Images
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.AIimage)

        @StyleableRes val index0 = 0
        @StyleableRes val index1 = 1
        @StyleableRes val index2 = 2

        val _text = typedArray.getString(index2)
        val _index = typedArray.getString(index1)
        val _defaultImage = typedArray.getString(index0)
        typedArray.recycle()

        restApiHandlerInterface.setRestApiUrl(BASE_URL);

        if (_defaultImage != "" && _defaultImage != null) {
            DEFAULT_IMAGE = _defaultImage.toString()
        }

        var sendInitRequest = false

        if (_text != null) {
            text = _text.toString()
            sendInitRequest = true
        }

        if (_index != null) {
            try {
                index = _index.toInt()
                sendInitRequest = true
            } catch (nfe: NumberFormatException) {
                // not a valid int
                Log.e(ERROR_LOG_TAG, "Initial index value not INT!")
            }
        }

        imageView = findViewById<ImageView>(R.id.imageView)

        if (sendInitRequest) {
            update()
        } else {
            loadImage(DEFAULT_IMAGE)
        }
    }
}


