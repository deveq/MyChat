package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_chat_list.*

class ChatListActivity : AppCompatActivity() {
    
    private val TAG = ChatListActivity::class.java.simpleName

//    val db = FirebaseFirestore.getInstance().collection("users")
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_list)

        auth = Firebase.auth

        if (auth.currentUser!= null) {
            chat_list_tv2.text = auth.currentUser.toString()
        }


        chat_list_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this,ChatMainActivity::class.java)
            startActivity(intent)

        }

        toFriends.setOnClickListener {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }




//        recyclerview_list
//
//        val userList = mutableListOf<User>()
//
//        val uid = auth.currentUser?.uid ?: ""
//        val adapter = RecyclerAdapter(userList) {
//            val intent = Intent(this, ChatRoomActivity::class.java)
//            startActivity(intent)
//        }
//
//
//
//        db.get()
//            .addOnSuccessListener {
//                for (i in it) {
//                    val userUid = (i.data["uid"]) as String
//                    val userName = (i.data["username"]) as String
//                    val user = User(userUid, userName)
//                    userList.add(user)
//                    Log.d(TAG,"로그인 후 DB 가져오기 성공")
//                }
//                adapter.notifyDataSetChanged()
//            }
//            .addOnFailureListener {
//
//                Log.d(TAG,"로그인 후 DB 가져오기 실패")
//            }
//
//        recyclerview_list.adapter = adapter
//        recyclerview_list.layoutManager = LinearLayoutManager(this)
//
//
//        val database = FirebaseDatabase.getInstance()
//        val myRef = database.getReference("message")

    }
}