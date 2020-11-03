package com.soldemom.mychat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.mychat.Model.Profile
import kotlinx.android.synthetic.main.friend_list_item.view.*

class FriendAdapter(var friendList: List<Profile>) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.friend_list_item,parent,false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val profile = friendList[position]
        holder.profileName.text = profile.nickname
        holder.profileContext.text = profile.text

    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage = itemView.profile_image
        val profileName = itemView.profile_name
        val profileContext = itemView.profile_context
    }


}

