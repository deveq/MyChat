package com.soldemom.mychat.main.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R

class FriendsListAdapter : RecyclerView.Adapter<FriendsListViewHolder>() {
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

    }

    override fun getItemCount(): Int {
        return friendsList.size
    }
}