package com.valenpatel.chataisol.interfaces.retrofit

import com.valenpatel.chataisol.model.ChatRequest
import com.valenpatel.chataisol.model.ChatResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIChatGPTApi {

    @Headers(

        "Content-Type: application/json"
    )
    @POST("v1/chat/completions")
    fun sendChatRequest(@Body request: ChatRequest): Call<ChatResponse>
}