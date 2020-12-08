package com.grebtsew.feedbackSDK

import org.json.JSONArray
import org.json.JSONObject

class RestApiHandler : RestApiHandlerInterface {

    override fun setRestApiUrl(defaulturl: String): String {
        return defaulturl
    }

    override fun parseResponseToUrl(json: JSONObject, index: Int): Pair<String, Int>? {


        var jsonArray = JSONArray(json.get("images").toString())
        if (jsonArray.length() == 0) {

            return null
        }

        val imageAmount = jsonArray.length()

        if (index > imageAmount - 1) {
            return null
        }

        var obj = JSONObject(jsonArray.get(index).toString())

        // If index is out of bounds!

        val url = obj.get("url")

        return Pair(url.toString(), imageAmount)
    }

}