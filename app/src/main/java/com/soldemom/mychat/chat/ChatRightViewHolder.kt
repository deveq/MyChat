package com.soldemom.mychat.chat

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_right_me.view.*

class ChatRightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val nameView = itemView.chat_room_my_name
    val messageView = itemView.chat_room_my_message
}