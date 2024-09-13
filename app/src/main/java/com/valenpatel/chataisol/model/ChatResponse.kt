package com.valenpatel.chataisol.model

import com.google.gson.annotations.SerializedName

data class ChatResponse(
    val id: String,
    @SerializedName("object") val objectType: String,
    val created: Long,
    val choices: List<Choice>
)

data class Choice(
    val message: Message,
    val finish_reason: String
)