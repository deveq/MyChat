package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

       /* val friendsRef = db.collection("friends")
            .document(auth.currentUser!!.uid)
            .collection("nicknames")
            .get()
            .addOnSuccessListener {
                for(i in it) {
                    nicknameList.add(i["nickname"] as String)
                    Log.d("friends열어보기",i["nickname"] as String)
                }
            }*/

        
        //친구목록 받아오기
        val bundle = intent.getBundleExtra("bundle")
        val list = bundle.getStringArrayList("nick")



        val profileRef = db.collection("nickname")
            .whereIn("nickname",list!!)
            .get()
            .addOnSuccessListener {
                Log.d("friend열어보기0","????뭐지")
                for(i in it) {
                    val profile = i.toObject(Profile::class.java)
                    profileList.add(profile)
                    Log.d("friends열어보기2",profile.nickname)
                }
                adapter.friendList = profileList
                adapter.notifyDataSetChanged()

            }
            .addOnFailureListener {
                Log.d("friends실패","왱?")
            }


        adapter = FriendAdapter(profileList)

        friend_recycler_view.adapter = adapter
        friend_recycler_view.layoutManager = LinearLayoutManager(this)




    }
}