package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_find_friend.*

class FindFriendActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    lateinit var friend: User
    lateinit var friendsList: MutableList<String>
    lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_friend)


        find_friend_input
        find_friend_button
        find_friend_no_result
        find_friend_image
        find_friend_name
        find_friend_introduce
        find_friend_id

        val toolbar = find_friend_toolbar
        toolbar.title = "친구 찾기"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("id")
        val myUid = intent.getStringExtra("uid")

        db.collection("users").document(myUid).get()
            .addOnSuccessListener {
                user = it.toObject(User::class.java)!!
                friendsList = user!!.friendsList
            }

        find_friend_id.text = id

        find_friend_button.setOnClickListener {
            val query = find_friend_input.text.toString()
            if (query != "" && query != user.id) {
                db.collection("id").document(query).get()
                    .addOnSuccessListener { snapshot ->
                        val uid = snapshot.getString("uid")
                        setFindResult(uid)
                    }
            } else if (query == user.id) {
                find_friend_no_result.apply {
                    text = "자신은 추가할 수 없습니다."
                    visibility = View.VISIBLE
                }

            }

        }

        find_friend_add_button.setOnClickListener {
            db.collection("users").document(myUid)
                .update("friendsList", FieldValue.arrayUnion(friend.uid))
                .addOnSuccessListener {
                    Toast.makeText(this, "추가 완료.", Toast.LENGTH_SHORT).show()
                    intent.putExtra("addedFriend",friend)
                    setResult(RESULT_OK,intent)
                    finish()
                }

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun setFindResult(uid: String?) {
        if (uid != null) {
            // id로 조회 시 나오는 값이 있을 경우
            db.collection("users").document(uid)
                .get()
                .addOnSuccessListener {

                    friend = it.toObject(User::class.java)!!
                    find_friend_view_group.visibility = View.VISIBLE
                    find_friend_no_result.visibility = View.GONE
                    find_friend_my_id.visibility = View.GONE
                    find_friend_name.text = friend.name
                    find_friend_introduce.text = friend.introduce
                    friend.image?.let { image ->
                        Glide.with(this)
                            .load(image)
                            .centerCrop()
                            .into(find_friend_image)
                    }

                    if (friendsList.contains(friend.uid)) {
                        // 이미 친구 목록에 있을 경우
                        find_friend_add_button.apply {
                            isClickable = false
                            text = "이미 등록되어있음"
                        }


                    } else {
                        // 없을 경우
                        find_friend_add_button.apply {
                            isClickable = true
                            text = "친구추가"
                        }


                    }

                }
        } else {
            // TODO id로 조회 시 나오는 값이 없을 경우
            find_friend_view_group.visibility = View.GONE
            find_friend_my_id.visibility = View.GONE
            find_friend_no_result.visibility = View.VISIBLE
        }
    }
}