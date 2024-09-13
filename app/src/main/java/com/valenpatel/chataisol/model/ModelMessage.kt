package com.valenpatel.chataisol.model

class ModelMessage(var message: String, var sentBy : String){
    companion object{
        val SENT_BY_ME = "Me"
        val SENT_BY_OTHER = "GPT"
    }
}