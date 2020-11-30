package com.soldemom.mychat.main.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.friends_list_item.view.*

class FriendsListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val imageView = itemView.friends_list_recycler_profile_img
    val nameView = itemView.friends_list_recycler_profile_name
    val introduceView = itemView.friends_list_recycler_introduce
}