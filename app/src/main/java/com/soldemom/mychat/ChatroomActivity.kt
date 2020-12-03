package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.chat.ChatRecyclerAdapter
import kotlinx.android.synthetic.main.activity_chatroom.*
import java.text.SimpleDateFormat

class ChatroomActivity : AppCompatActivity() {

    val realtimeDB = FirebaseDatabase.getInstance()
    lateinit var myUser: User
    lateinit var destinationUser: User
    var chatroomId: String? = null
    var chatroom: Chatroom? = null
    lateinit var chatRef: DatabaseReference
    lateinit var chatRecyclerAdapter: ChatRecyclerAdapter
    lateinit var getCommentRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)

        myUser = intent.getSerializableExtra("myUser") as User
        destinationUser = intent.getSerializableExtra("destinationUser") as User
        chatroomId = intent.getStringExtra("chatroomId")

        // 친구목록을 통해 들어온거라면 chatroomId가 없으므로
        //서버에 만들어진 chatroom이 있는지 확인
        checkChatRoom()

        val users = hashMapOf<String, User>(
            "myUser" to myUser,
            "destinationUser" to destinationUser
        )


        // TODO 채팅목록을 통해 들어왔을 경우 chatroomId를 바로 intent에서 받아와서 넣어줘야함.


        chatroom_recycler_view.layoutManager = LinearLayoutManager(this)
        chatRecyclerAdapter = ChatRecyclerAdapter()
        chatRecyclerAdapter.users = users
        chatroom_recycler_view.adapter = chatRecyclerAdapter

        chatroom_send_button.setOnClickListener {
            val text = chatroom_edit_text.text.toString()
            if (text != "") {
                val commentsRef = realtimeDB.getReference("chatrooms")
                    .child(chatroomId!!).child("comments")

                val comment: Chatroom.Comment = Chatroom.Comment()
                comment.senderUid = myUser.uid
                comment.text = text
                comment.timestamp = ServerValue.TIMESTAMP
                commentsRef.push().setValue(comment).addOnSuccessListener {
                    chatroom_edit_text.setText("")
                }
            }
        }


    }

    fun checkChatRoom() {
        //대화방에 참여한 user목록에 현재 user의 uid가 true인 chatroom객체들을 가져옴
        realtimeDB.getReference("chatrooms").orderByChild("users/${myUser.uid}").equalTo(true)
            // addListenerForSingleValueEvent는 1번만 수신하고 이후에 추가로 수신하지 않음.
            .addListenerForSingleValueEvent(object : ValueEventListener {
                // chatroom객체들을 가지고있는 snapshot을 가지고있는 onDataChange
                override fun onDataChange(snapshot: DataSnapshot) {
                    val readUsersMap = hashMapOf<String, Any>()
                    // chatroom객체를 하나씩 for문 돌려줌
                    for (item in snapshot.children) {
//                        val key = item.key
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

            Log.d("chat", "if문 시작")
            chatRef = realtimeDB.getReference("chatrooms").push()
            chatRef.setValue(chatroom).addOnSuccessListener {
                chatroomId = chatRef.key
                myUser.chatList.add(chatroomId!!)
                //TODO Firestore / users / uid / chatList에 chatroomId 추가해줘야함.
                val db = FirebaseFirestore.getInstance()
                val myUserRef =
                    db.collection("users").document(myUser.uid)
                val destUserRef =
                    db.collection("users").document(destinationUser.uid)

                db.runBatch { batch ->
                    batch.update(myUserRef, "chatList", FieldValue.arrayUnion(chatroomId))
                    batch.update(destUserRef, "chatList", FieldValue.arrayUnion(chatroomId))
                }.addOnSuccessListener {
                    Log.d("chat", "transaction 둘 다 성공")

                }

                // Firebase와의 연동이 비동기로 이루어지기 때문에 순차적으로 끝나지 않아서
                // 각각 getComments()를 넣어줌
                getComments()
            }
        } else {
            Log.d("chat", "if문 시작 (else)")
            getComments()

        }


    }

    fun getComments() {
        //TODO chatroomId가 nullPointerException이 뜸
        getCommentRef = realtimeDB.getReference("chatrooms").child(chatroomId!!).child("comments")
        getCommentRef.addValueEventListener(getCommentsEventListener)
//        getCommentRef.addListenerForSingleValueEvent(getCommentsEventListener)
    }

    val getCommentsEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val commentsList = mutableListOf<Chatroom.Comment>()
            val readUsersMap = hashMapOf<String, Any>()
            var flag = false
            var timeTemp: String? = null
            val sdf = SimpleDateFormat("MMdd")
            for (data in snapshot.children) {
                val comment = data.getValue(Chatroom.Comment::class.java)
                val key = data.key
                comment?.let {

                    if (timeTemp == null || timeTemp != sdf.format(it.timestamp)) {
                        timeTemp = sdf.format(it.timestamp)
                        val time = Chatroom.Comment()
                        time.senderUid = "시간"
                        time.timestamp = it.timestamp
                        commentsList.add(time)
                    }

                    if (it.readUsers[myUser.uid] == null) {
                        it.readUsers[myUser.uid] = true
                        readUsersMap[key!!] = comment
                        flag = true
                    }

                    commentsList.add(it)
                }
            }
            if (flag) {
                realtimeDB.getReference("chatrooms")
                    .child(chatroomId!!)
                    .child("comments")
                    .updateChildren(readUsersMap)
                    /*.addOnCompleteListener {
                        //얘는 계속 되어야함.
                        chatRecyclerAdapter.comments = commentsList
                        chatRecyclerAdapter.notifyDataSetChanged()
                        chatroom_recycler_view.scrollToPosition(chatRecyclerAdapter.comments.size - 1)
                    }*/
            }

            //comment를 읽어오기 위해서는 지속감시해야함
            //comment를 읽은 후
            //읽음 표시 기능
            //만약 readUsers에 내가 있다면 하지않고, 없다면 하기
            chatRecyclerAdapter.comments = commentsList
            chatRecyclerAdapter.notifyDataSetChanged()
            chatroom_recycler_view.scrollToPosition(chatRecyclerAdapter.comments.size - 1)

        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        getCommentRef.removeEventListener(getCommentsEventListener)
        //액티비티를 벗어나도 계속 갖고오는거 중단.

    }
}