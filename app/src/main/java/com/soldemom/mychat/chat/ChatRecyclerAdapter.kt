package com.soldemom.mychat.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.R

class ChatRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var comments = mutableListOf<Chatroom.Comment>()
    var names = hashMapOf<String,String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_right_me, parent, false)
        return ChatRightViewHolder(view)

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = comments[position]
        (holder as ChatRightViewHolder).nameView.text = names[comment.senderUid]
        holder.messageView.text = comment.text

    }

    override fun getItemCount(): Int {
        return comments.size
    }
}