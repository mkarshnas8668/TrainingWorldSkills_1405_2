package com.mkarshnas6.karenstudio.worldskill.data.remote.SSE

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class SSEManager {
    //......... main variables .................
    private val client = OkHttpClient
        .Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    // interface listener
    interface SEEListener {
        fun onConnected()
        fun onMessage(message: String)
        fun onError(error: String)
    }

    private var listener: SEEListener? = null

    // know which screen . set listener
    fun setListener(listener: SEEListener) {
        this.listener = listener
    }

    // connected to SSE
    fun connect(clientId: String, token: String = "") {
        job?.cancel() // cancel last work

        job = scope.launch {
            try {
                val url = "http://10.0.2.2:8000/sse/subscribe/$clientId"
                Log.d("SEE", "connecting to server ...")

                val requestBuilder = Request.Builder()
                    .url(url)
                    .addHeader("Accept", "text/event-stream")
                    .addHeader("Cache-Control", "no-cache")

                if (token.isNotBlank()) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                // send request
                val response = client.newCall(requestBuilder.build()).execute()

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        listener?.onConnected()
                        Log.d("SSE", "connected !!")
                    }

                    // now read messages
                    val reader = BufferedReader(
                        InputStreamReader(response.body?.byteStream())
                    )

                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        if (line?.startsWith("data:") == true) {
                            val jsonData = line!!.removePrefix("data:").trim()

                            Log.d("SEE", "reserved : $jsonData")

                            withContext(Dispatchers.Main) {
                                listener?.onMessage(jsonData)
                            }
                        }
                    }

                } else {
                    withContext(Dispatchers.Main) {
                        listener?.onError("http error : ${response.code}")
                    }
                }

            } catch (e: Exception) {
                Log.e("SSE", "error : ${e.message}")
                withContext(Dispatchers.Main) {
                    listener?.onError(e.message ?: "Unknown Error !")
                }
            }
        }

    }

    //........... disconnect .................
    fun disconnect() {
        job?.cancel()
        job = null
        Log.d("SSE", "Disconnected !!")
    }

}