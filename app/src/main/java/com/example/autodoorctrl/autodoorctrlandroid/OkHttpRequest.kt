package com.example.autodoorctrl.autodoorctrlandroid
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object OkHttpRequest{
    private val client = OkHttpClient()

    private const val BASE_URL = "https://rpiadc.com/api/"

    fun get(endpoint: String, callback: Callback): Call {
        val request = Request.Builder()
            .url("${BASE_URL}$endpoint")
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }
    fun post(endpoint: String, parameters: HashMap<String, String>, callback: Callback): Call {
        val parameter = JSONObject(parameters)
        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val requestBody = parameter.toString().toRequestBody(mediaTypeJson)
        val request = Request.Builder()
            .url("${BASE_URL}$endpoint")
            .post(requestBody)
            .addHeader("content-type", "application/json; charset=utf-8")
            .build()


        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }
}