package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat_main.*
import kotlinx.android.synthetic.main.activity_login.*

class ChatMainActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    val nicknameList = arrayListOf<String>("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_main)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()

        //만약 currentUser가 있다면 ChatListActivity류ㅗ
        if (auth.currentUser != null) {
            val intent = Intent(this, FriendsListActivity::class.java)
            startActivity(intent)
        }

        login_btn.setOnClickListener {
            val email = login_id_et.text.toString()
            val password = login_pw_et.text.toString()

            if (email!="" && password != "") {
                auth.signInWithEmailAndPassword(email,password)
                    .addOnSuccessListener {


                        val friendsRef = db.collection("friends")
                            .document(auth.currentUser!!.uid)
                            .collection("nicknames")
                            .get()
                            .addOnSuccessListener {
                                for(i in it) {
                                    nicknameList.add(i["nickname"] as String)
                                    Log.d("friends열어보기",i["nickname"] as String)
                                }
                            }

                        val intent = Intent(this, FriendsListActivity::class.java)
//                        intent.putExtra("nicknames",nicknameList)
                        val bundle = Bundle()
                        bundle.putStringArrayList("nick",nicknameList)
                        intent.putExtra("bundle",bundle)
                        startActivity(intent)

                    }
            }
        }
        login_join_btn.setOnClickListener {
            val intent = Intent(this, JoinActivity::class.java)
            startActivity(intent)
        }


    }
}