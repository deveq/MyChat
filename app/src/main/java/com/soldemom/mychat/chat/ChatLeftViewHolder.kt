package com.soldemom.mychat.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chatroom_left_chat_item.view.*

class ChatLeftViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    val timeView = itemView.left_chat_time
    val messageView = itemView.left_chat_text
    val nameView = itemView.left_chat_name


}