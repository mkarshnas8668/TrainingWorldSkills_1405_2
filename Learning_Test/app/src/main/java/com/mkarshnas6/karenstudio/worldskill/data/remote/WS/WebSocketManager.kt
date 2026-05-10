package com.mkarshnas6.karenstudio.worldskill.data.remote.WS

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

class WebSocketManager {

    //......... main variable ...........
    private var webSocket: WebSocket? = null
    private val gson = Gson()
    private var listener: WebSocketListener? = null
    private var myClientId: String = ""

    //............ data class ...................
    // send message for chat
    data class ChatMessage(
        val type: String = "chat",
        val receiver_id: String,
        val message: String
    )

    // type of typing
    data class TypingMessage(
        val type: String = "typing",
        val receiver_id: String,
        val is_typing: Boolean
    )

    // request to get all online users
    data class OnlineUsersRequest(
        val type: String = "get_online_users"
    )

    // response to get all online users
    data class OnlineUsersResponse(
        val type: String,
        val users: List<String>,
        val count: Int
    )

    // request get message
    data class IncomingChatMessage(
        val type: String,
        val sender_id: String,
        val message: String,
        val timestamp: String
    )

    // request who typing
    data class TypingNotification(
        val type: String,
        val user_id: String,
        val is_typing: Boolean
    )

    //   ...........  interface listener
    interface WebSocketListener {
        fun onConnected(clientId: String)
        fun onDisconnected()
        fun onError(error: String)
        fun onChatMessage(message: IncomingChatMessage)
        fun onOnlineUsers(users: OnlineUsersResponse)
        fun onTypingNotification(typing: TypingNotification)
    }

    //    ................. set listener ....................
    fun setListener(listener: WebSocketListener) {
        this.listener = listener
    }

    //    ............... connected to server .......................
    fun connect(clientType: String, clientId: String, token: String = "") {
        this.myClientId = clientId // save name

        val url = "ws://10.0.2.2:8000/ws/$clientType/$clientId"
        Log.d("WEB_SOCKET", "connecting : $url")

        val requestBuilder = Request.Builder().url(url)
        if (token.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer $token") // if have token added to header
        }

        // create client ok http without limit time out because websocket opening always
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS) // zero mean always
            .build()

        webSocket = client.newWebSocket(
            requestBuilder.build(),
            object : okhttp3.WebSocketListener() {

                // when connected
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("WEB_SOCKET", "connected to server !!")
                    listener?.onConnected(myClientId)
                }

                // when get message
                override fun onMessage(webSocket: WebSocket, text: String) {
                    Log.d("WEB_SOCKET", "get message : $text")
                    handleMessage(text)
                }

                // when disconnected
                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    Log.d("WEB_SOCKET", "desconnect : $reason")
                    listener?.onDisconnected()
                }

                // when get error
                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("WEB_SOCKET", "desconnect : ${t.message}")
                    listener?.onError(t.message ?: "Unknown Error !!")
                }

            }
        )
    }

    // handel incoming message : organized every thing get of server
    private fun handleMessage(jsonString: String) {
        try {
            // converting json to object for reading type
            val jsonObject = gson.fromJson(jsonString, JsonObject::class.java)

            // read type : a key for know type message nad handled
            val type = jsonObject.get("type")?.asString

            when (type) {
                "chat_message" -> {
                    // get new message
                    val msg = gson.fromJson(jsonString, IncomingChatMessage::class.java)
                    listener?.onChatMessage(msg)
                }

                "online_users" -> {
                    // get list of online users
                    val users = gson.fromJson(jsonString, OnlineUsersResponse::class.java)
                    listener?.onOnlineUsers(users)
                }

                "typing" -> {
                    val typing = gson.fromJson(jsonString, TypingNotification::class.java)
                    listener?.onTypingNotification(typing)
                }

                else -> {
                    Log.e("WEB_SOCKET", "type of Unknown message : $type")
                }
            }

        } catch (e: Exception) {
            Log.e("WEB_SOCKET", "error in analyzing message ${e.message}")
        }
    }


    // .............. fun sending message ...............
    // call this fun for sending message to server

    // sending message in chat
    fun sendingMessage(receiverId: String, message: String) {
        val chatMsg = ChatMessage(
            receiver_id = receiverId,
            message = message
        )
        sendJson(chatMsg)
    }

    // sending status typing
    fun sendTypingStatus(receiverId: String, isTyping: Boolean) {
        val typingMsg = TypingMessage(
            receiver_id = receiverId,
            is_typing = isTyping
        )
        sendJson(typingMsg)
    }

    // request get online users
    fun requestOnlineUsers() {
        val request = OnlineUsersRequest()
        sendJson(request)
    }

    // converter object to json and send
    private fun sendJson(obj: Any) {
        val json = gson.toJson(obj)
        Log.d("WEB_SOCKET", "sent : $json")
        webSocket?.send(json)
    }

    // disconnect connection
    fun disconnect() {
        webSocket?.close(1000, "exit user") // status code 1000 mean normal disconnect
        webSocket = null
    }

}