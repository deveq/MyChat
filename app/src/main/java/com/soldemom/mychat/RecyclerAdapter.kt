package com.soldemom.mychat

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.message_list.view.*

class RecyclerAdapter(val userList: MutableList<User>,
                      val itemClickListener: (View) -> Unit

) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.message_list,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val user = userList[position]
        holder.listName.text = user.username
        holder.listMessage.text = user.uid
        holder.itemView.setOnClickListener {
            itemClickListener
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val listName = itemView.list_name
        val listMessage = itemView.list_message

    }
}