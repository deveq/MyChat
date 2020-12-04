package com.soldemom.mychat.main.recycler

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soldemom.mychat.ChatroomActivity
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R

class FriendsListAdapter(val startChat:(User)->Unit) : RecyclerView.Adapter<FriendsListViewHolder>() {
    var friendsList =  mutableListOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.friends_list_item,parent,false)
        return FriendsListViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendsListViewHolder, position: Int) {
        val friend = friendsList[position]
        friend.introduce?.let {
            holder.introduceView.text = friend.introduce
        }

        holder.nameView.text = friend.name
        friend.image?.let {
            Glide.with(holder.itemView)
                .load(friend.image)
                .centerCrop()
                .into(holder.imageView)
        }

        holder.itemView.setOnClickListener {
            // RecyclerView를 가진 FriendsListFragment에서 만든 고차함수
            // 나의 uid와 상대방의 uid를 받아서 intent에 putExtra 한 후
            // ChatroomActivity로의 intent 실행
            startChat(friend)
        }
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }
}