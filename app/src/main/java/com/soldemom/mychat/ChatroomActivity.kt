package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.chat.ChatRecyclerAdapter
import kotlinx.android.synthetic.main.activity_chatroom.*

class ChatroomActivity : AppCompatActivity() {

    val realtimeDB = FirebaseDatabase.getInstance()
    lateinit var myUser: User
    lateinit var destinationUser: User
    var chatroomId: String? = null
    var chatroom: Chatroom? = null
    lateinit var chatRef: DatabaseReference
    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        myUser = intent.getSerializableExtra("myUser") as User
        destinationUser = intent.getSerializableExtra("destinationUser") as User

//        val chatroom = Chatroom()
//        chatroom.users[myUser.uid] = true
//        chatroom.users[destinationUser.uid] = true



        checkChatRoom()

        val userNames = hashMapOf<String,String>(
            myUser.uid to myUser.name,
            destinationUser.uid to destinationUser.name
        )






        // TODO 채팅목록을 통해 들어왔을 경우 chatroomId를 바로 intent에서 받아와서 넣어줘야함.



        chatroom_recycler_view.layoutManager = LinearLayoutManager(this)
        chatRecyclerAdapter = ChatRecyclerAdapter()
        chatRecyclerAdapter.names = userNames
        chatroom_recycler_view.adapter = chatRecyclerAdapter

        chatroom_send_button.setOnClickListener {
            val text = chatroom_edit_text.text.toString()
            if (text != "") {
                val commentsRef = realtimeDB.getReference("chatrooms")
                    .child(chatroomId!!).child("comments")

                val comment : Chatroom.Comment = Chatroom.Comment()
                comment.senderUid = myUser.uid
                comment.text = text
//                comment.timestamp = ServerValue.TIMESTAMP
                commentsRef.push().setValue(comment)
            }
        }




    }

    fun checkChatRoom() {
        //대화방에 참여한 user목록에 현재 user의 uid가 true인 chatroom객체들을 가져옴
        realtimeDB.getReference("chatrooms").orderByChild("users/${myUser.uid}").equalTo(true)
                // addListenerForSingleValueEvent는 1번만 수신하고 이후에 추가로 수신하지 않음.
            .addListenerForSingleValueEvent(object : ValueEventListener{
                // chatroom객체들을 가지고있는 snapshot을 가지고있는 onDataChange
                override fun onDataChange(snapshot: DataSnapshot) {
                    // chatroom객체를 하나씩 for문 돌려줌
                    for (item in snapshot.children) {
                        val temp = item.getValue(Chatroom::class.java)
                        //chatroom에 상대방의 uid도 존재한다면 방이 이미 존재한다는 의미이므로
                        if (temp!!.users!!.contains(destinationUser.uid)) {
                            chatroomId = item.key!!
                            chatroom = temp
                        }
                    }
                    getChatroom()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun getChatroom() {
        // 친구목록을 통해 들어왔을 경우 chatroomId가 없는 상태
        if (chatroomId == null) {

            chatroom = Chatroom().also {
                it.users[myUser.uid] = true
                it.users[destinationUser.uid] = true
            }

            Log.d("chat","if문 시작")
            chatRef = realtimeDB.getReference("chatrooms").push()
            chatRef.setValue(chatroom).addOnSuccessListener {
                chatroomId = chatRef.key
                myUser.chatList.add(chatroomId!!)
                //TODO Firestore / users / uid / chatList에 chatroomId 추가해줘야함.
            }
        } else {
            Log.d("chat","if문 시작 (else)")

        }
        realtimeDB.getReference("chatrooms").child(chatroomId!!).child("comments")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentsList = mutableListOf<Chatroom.Comment>()
                    for (data in snapshot.children) {
                        val comment = data.getValue(Chatroom.Comment::class.java)
                        commentsList.add(comment!!)
                    }
                    chatRecyclerAdapter.comments = commentsList
                    chatRecyclerAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


    }

}