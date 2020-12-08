package com.grebtsew.feedbackSDK

import org.json.JSONObject


interface RestApiHandlerInterface {


    fun setRestApiUrl(defaulturl: String): String;
    /**
     * Return base url to new REST api
     */


    fun parseResponseToUrl(json: JSONObject, index: Int): Pair<String, Int>?
    /**
     * Write function that parses the response JSON from REST Api url and returns url to image
     * @Return Pair<String, Int> -> url, amount of images found
     */
}