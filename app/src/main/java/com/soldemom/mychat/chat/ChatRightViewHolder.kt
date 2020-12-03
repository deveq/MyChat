package com.soldemom.mychat.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chatroom_right_chat_item.view.*

class ChatRightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val timeView = itemView.right_chat_time
    val messageView = itemView.right_chat_text
    val readView = itemView.right_chat_read

}