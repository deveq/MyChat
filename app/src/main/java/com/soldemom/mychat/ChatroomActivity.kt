package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.FcmReqModel
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.chat.ChatRecyclerAdapter
import com.soldemom.mychat.fcm.RetrofitHelper
import com.soldemom.mychat.fcm.RetrofitService
import kotlinx.android.synthetic.main.activity_chatroom.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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

        val retrofit = RetrofitHelper.getRetrofit()
        val retrofitService = retrofit.create(RetrofitService::class.java)

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
                //여기에서 Chatroom객체의 timestamp도 바꿔줘야함.
//                commentsRef.push().setValue(comment).addOnSuccessListener {
//                    chatroom_edit_text.setText("")
//                }

                val chatroomRef = realtimeDB.getReference("chatrooms").child(chatroomId!!)
                chatroomRef.updateChildren(hashMapOf<String, Any>(
                        "timestamp" to ServerValue.TIMESTAMP,
                        "comments/${chatroomRef.push().key}" to comment
                    )).addOnSuccessListener {
                    chatroom_edit_text.setText("")
                }


//                "comments/${commentsRef.push().key}" to comment
//                "timestamp" to ServerValue.TIMESTAMP




                destinationUser.fcmToken?.let {
                    val req = FcmReqModel(it, myUser.name, text)
                    retrofitService.requestPush(req).enqueue(object : Callback<String> {
                        override fun onResponse(call: Call<String>, response: Response<String>) {
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            t.printStackTrace()
                        }
                    })

                }
            }
        } // send_button의 onClickListener - 끝


        //window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        //이건 했더니 recyclerView 최상단이 짤림... ㅠㅠ
        //recyclerView 뿐만 아니라 모든 뷰의 포지션이 변경될때 사용할 수 있음!
        chatroom_recycler_view.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                val rv = chatroom_recycler_view
                if (rv.adapter!!.itemCount != 0) {
                    rv.smoothScrollToPosition(rv.adapter!!.itemCount -1)
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
            }
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
    }
}