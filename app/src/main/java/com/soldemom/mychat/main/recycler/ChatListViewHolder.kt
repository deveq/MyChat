package com.soldemom.mychat.main.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_list_item.view.*

class ChatListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    // image name text time
    val imageView = itemView.chat_list_image
    val nameView = itemView.chat_list_name
    val lastTextView = itemView.chat_list_last_text
    val timeView = itemView.chat_list_time
}