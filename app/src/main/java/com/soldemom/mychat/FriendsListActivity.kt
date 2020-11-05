package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.Model.Profile
import kotlinx.android.synthetic.main.activity_friends_list.*
import java.util.ArrayList

class FriendsListActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var nicknameList: ArrayList<String>
    val profileList = mutableListOf<Profile>()
    lateinit var adapter: FriendAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_list)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid


        val friendsList = arrayListOf<String>()

        // 1. 친구 목록 받아오기
        db.collection("friends").document(uid).collection("nicknames").get()
            .addOnSuccessListener {
                //친구 목록을 받은 뒤 friendsList로 넣어줌
                for (i in it) {
                    friendsList.add(i["nickname"] as String)
                }

                //friendsList에 넣은 후 해당 목록을 토대로 친구의 프로필을 받아옴.
                db.collection("nickname").whereIn("nickname",friendsList).get()
                    .addOnSuccessListener {
                        //친구의 프로필을 정상적으로 받아왔다면, adapter의 friendList에 넣어줌.
                        adapter.friendList = it.toObjects(Profile::class.java)
                        //넣어준 후 recycler view의 데이터가 변경되었음을 알려줌.
                        adapter.notifyDataSetChanged()
                    }
            }



        adapter = FriendAdapter(profileList)

        friend_recycler_view.adapter = adapter
        friend_recycler_view.layoutManager = LinearLayoutManager(this)




    }
}