package com.example.autodoorctrl.autodoorctrlandroid
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Callback
import okhttp3.Call
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody



class OkHttpRequest(private var client: OkHttpClient) {
    fun get(url: String, callback: Callback): Call {
        val request = Request.Builder()
            .url(url)
            .build()

        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }
    fun post(url: String, parameters: HashMap<String, String>, callback: Callback): Call {
        val parameter = JSONObject(parameters)
        val mediaTypeJson = "application/json; charset=utf-8".toMediaType()
        val requestBody = parameter.toString().toRequestBody(mediaTypeJson)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("content-type", "application/json; charset=utf-8")
            .build()


        val call = client.newCall(request)
        call.enqueue(callback)
        return call
    }
}