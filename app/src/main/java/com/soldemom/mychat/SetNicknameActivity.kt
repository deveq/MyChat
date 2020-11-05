package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.Model.Profile
import kotlinx.android.synthetic.main.activity_set_nickname.*

class SetNicknameActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    val nicknameList = mutableListOf<String>()
    lateinit var nickname: String
    var checkFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_nickname)



        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        val myRef = db.collection("nickname")

/*        myRef.get().addOnSuccessListener {
            for (i in it) {
                nicknameList.add(i["nickname"] as String)
            }
        }*/

        val user = auth.currentUser
        val uid = user?.uid

        nickname_duplication_check.setOnClickListener {
            nickname = nickname_et.text.toString()

            myRef.whereEqualTo("nickname",nickname)
                .get()
                .addOnSuccessListener {
                    if (it.size() == 0){
                        nickname_duplication_check.text = "사용가능"
                        checkFlag = true
                    } else {
                        val dialog = AlertDialog.Builder(this)
                        dialog.setMessage("이미 사용중이거나 사용할 수 없는 닉네임입니다.")
                            .setPositiveButton("확인",null)
                            .show()
                        nickname_duplication_check.text = "중복체크"
                        checkFlag = false
                    }

                }
                .addOnFailureListener { 
                    Toast.makeText(this,"실패했음, ${it.toString()}, ${it.message}",Toast.LENGTH_SHORT).show()

                    
                }

//            if (!nickname.contains(nickname)) {
//                nickname_duplication_check.text = "사용가능"
//            } else {
//
//            }
        }

        nickname_confirm_button.setOnClickListener {
            if (checkFlag) {
                val nicknameObject = Profile(nickname, uid!!)
                myRef.add(nicknameObject)
                    .addOnSuccessListener {
                        val intent = Intent(this, FriendsListActivity::class.java)
                        startActivity(intent)
                    }
            } else {
                val dialog = AlertDialog.Builder(this)
                dialog.setMessage("닉네임 중복 체크를 해주세요.")
                    .setPositiveButton("확인",null)
                    .show()
            }


        }







    }
}