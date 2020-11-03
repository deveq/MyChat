package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth



        join_button_main.setOnClickListener {
            val email = email_area.text.toString()
            val password = password_area.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid ?: ""

                        val user = User(uid,username.text.toString())

                        //데이터베이스에 유저 정보 넣기
                        val db = FirebaseFirestore.getInstance().collection("users")
                        db.document(auth.uid.toString())
                            .set(user)
                            .addOnSuccessListener {
                                Toast.makeText(this,"데이터베이스 성공",Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this,"데이터베이스 실패",Toast.LENGTH_SHORT).show()

                            }

                    } else {
                        Toast.makeText(this,"걍 실패",Toast.LENGTH_SHORT).show()
                    }

                    // ...
                }
        }


        login_button_main.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }
}