package com.soldemom.mychat.chat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R
import java.text.SimpleDateFormat

class ChatRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var comments = mutableListOf<Chatroom.Comment>()
    var users = hashMapOf<String, User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Chatroom.Comment.CHAT_LEFT -> {
                val view = inflater
                    .inflate(R.layout.chatroom_left_chat_item,parent,false)
                ChatLeftViewHolder(view)
            }
            Chatroom.Comment.CHAT_RIGHT -> {
                val view = inflater
                    .inflate(R.layout.chatroom_right_chat_item,parent,false)
                ChatRightViewHolder(view)
            }
            else -> {
                val view = inflater
                    .inflate(R.layout.chatroom_date_list_item, parent, false)
                ChatTimeViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val comment = comments[position]
        val sdf = SimpleDateFormat("HH:mm")

        when (comment.senderUid) {
            users["myUser"]!!.uid -> {
                holder as ChatRightViewHolder
                holder.messageView.text = comment.text
                holder.timeView.text = sdf.format(comment.timestamp)

                if (comment.readUsers.size == 2) {
                    holder.readView.visibility = View.INVISIBLE
                } else {
                    holder.readView.visibility = View.VISIBLE
                }

            }
            users["destinationUser"]!!.uid -> {
                holder as ChatLeftViewHolder
                holder.messageView.text = comment.text
                holder.timeView.text = sdf.format(comment.timestamp)
                holder.nameView.text = users["destinationUser"]!!.name
            }
            else -> {
                val dateFormat = SimpleDateFormat("yyyy. MM. dd")
                holder as ChatTimeViewHolder
                holder.timeView.text = dateFormat.format(comment.timestamp)
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemViewType(position: Int): Int {
        val comment = comments[position]

        return when (comment.senderUid) {
            users["myUser"]!!.uid -> Chatroom.Comment.CHAT_RIGHT
            users["destinationUser"]!!.uid -> Chatroom.Comment.CHAT_LEFT
            else -> Chatroom.Comment.TIME
        }
    }
}