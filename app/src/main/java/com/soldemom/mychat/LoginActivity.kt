package com.soldemom.mychat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.join_layout.view.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private var db = FirebaseFirestore.getInstance()

    private val TAG: String = LoginActivity::class.java.simpleName
    lateinit var joinView: View
    lateinit var toMainIntent: Intent


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        // MainActivity로 가는 intent
        toMainIntent = Intent(this, MainActivity::class.java)

        //로그인이 되어있는 상태라면 바로 MainActivity로
        auth.currentUser?.let {
            startActivity(toMainIntent)
            finish()
        }

        joinView = LayoutInflater.from(this).inflate(R.layout.join_layout, null)

        email_input
        pw_input
        login_btn
        join_btn
        google_login

        join_btn.setOnClickListener {
            setJoinButton()
        }

        login_btn.setOnClickListener {
            setLoginButton()
        }

    }

    private fun setLoginButton() {
        val email = email_input.text.toString()
        val pw = pw_input.text.toString()
        auth.signInWithEmailAndPassword(email, pw)
            .addOnSuccessListener {
                startActivity(toMainIntent)
                finish()
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).setTitle("로그인실패").setPositiveButton("확인", null)
                    .create().show()
            }
    }

    private fun setJoinButton() {
        if (joinView.parent != null) {
            (joinView.parent as ViewGroup).removeView(joinView)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(joinView)
            .create()

        joinView.join_close.setOnClickListener {
            dialog.cancel()
        }

        joinView.join_button.setOnClickListener {
            val email = joinView.join_id_input.text.toString()
            val password = joinView.join_password.text.toString()
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val name = joinView.join_name_input.text.toString()
                    val user = User(it.user!!.uid, name)

                    db.collection("users").document(user.uid)
                        .set(user)
                        .addOnSuccessListener {
                            Toast.makeText(this, "가입성공", Toast.LENGTH_SHORT).show()
                            startActivity(toMainIntent)
                            finish()
                        }
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this)
                        .setTitle("가입실패")
                        .setMessage(it.message)
                        .setPositiveButton("확인", null)
                        .create().show()
                }
        }

        dialog.show()
    }

    //현재 기기에서 사용중인 토큰 확인인
   fun getToken() {
        //8분 25초
    }
}