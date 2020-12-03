package com.soldemom.mychat.main.recycler

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R
import java.text.SimpleDateFormat
import java.util.*

class ChatListAdapter(val myUser: User,
                      val startChat: (User, User, String) -> Unit
) : RecyclerView.Adapter<ChatListViewHolder>() {
    var chatrooms = hashMapOf<String, Chatroom>()
    val db = FirebaseFirestore.getInstance()
    val sdf = SimpleDateFormat("MM월 dd일")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.chat_list_item,parent,false)
        return ChatListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        // TODO chatList<String> 객체안에 있는 chatroomId를 이용해서 서버에서 Chatroom객체를 받아옴.
        // 내 uid와 같지 않은 uid를 얻은 다음 해당 uid의 User객체를 firestore에서 얻음
        val chatroomId = myUser.chatList[position]
        val chatroom = chatrooms[chatroomId]
        var destUser: User
        var destinationUid: String = ""
        for (i in chatroom!!.users) {
            if (i.key != myUser.uid) {
                destinationUid = i.key
                break
            }
        }
        db.collection("users").document(destinationUid).get()
            .addOnSuccessListener {
                destUser = it.toObject(User::class.java)!!
                destUser.apply{
                    //프사 설정
                    image?.let {
                        Glide.with(holder.imageView)
                            .load(destUser.image)
                            .centerCrop()
                            .into(holder.imageView)
                    }
                    //이름 설정
                    holder.nameView.text = name
                    //채팅 아이템 터치 시 ChatroomActivity로 가는 intent실행
                }
                holder.itemView.setOnClickListener {
                    startChat(myUser,destUser,chatroomId)
                }
            }
        // TODO chatroom에 comment가 0개 일때 처리해야함

        if (chatroom.comments.size != 0) {
            val commentMap = TreeMap<String, Chatroom.Comment>(Collections.reverseOrder())
            commentMap.putAll(chatroom.comments)
            val lastMessageKey = commentMap.firstKey()
            val comment = chatroom.comments[lastMessageKey]!!
            holder.lastTextView.text = comment.text
            holder.timeView.text = sdf.format(comment.timestamp)
        }
        

    }

    override fun getItemCount(): Int {
        return myUser.chatList.size
    }
}