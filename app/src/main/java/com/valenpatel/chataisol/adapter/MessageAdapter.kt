package com.valenpatel.chataisol.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.valenpatel.chataisol.R
import com.valenpatel.chataisol.model.ModelMessage

class MessageAdapter(private val context: Context, private val messageList: ArrayList<ModelMessage>) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(currentItem: ModelMessage) {
           var txtMessage = itemView.findViewById<TextView>(R.id.chat_layoutTxtMessage)
           var txtResponse = itemView.findViewById<TextView>(R.id.chat_layoutTxtResponse)
           var viewChatLayout4Border = itemView.findViewById<View>(R.id.view_chat_layout)
           var imageChatAI = itemView.findViewById<ImageView>(R.id.imageView)

            if (currentItem.sentBy == ModelMessage.SENT_BY_ME) {
                txtMessage.visibility = View.VISIBLE
                txtResponse.visibility = View.GONE
                txtMessage.text = currentItem.message
                viewChatLayout4Border.visibility = View.GONE
                imageChatAI.visibility = View.GONE
            } else {
                txtMessage.visibility = View.GONE
                txtResponse.visibility = View.VISIBLE
                txtResponse.text = currentItem.message
                viewChatLayout4Border.visibility = View.VISIBLE
                imageChatAI.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        return MessageViewHolder(
            LayoutInflater.from(context).inflate(R.layout.chat_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messageList[position])

        if(messageList[position].sentBy == ModelMessage.SENT_BY_ME){
            holder.itemView.findViewById<TextView>(R.id.chat_layoutTxtMessage).text = messageList[position].message
        }else if(messageList[position].sentBy == ModelMessage.SENT_BY_OTHER){
            holder.itemView.findViewById<TextView>(R.id.chat_layoutTxtResponse).text = messageList[position].message
        }
    }
}