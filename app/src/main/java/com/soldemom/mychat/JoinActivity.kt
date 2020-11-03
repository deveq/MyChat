package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_join.*

class JoinActivity : AppCompatActivity() {

    lateinit var auth : FirebaseAuth
    var emailFlag = false
    var passwordFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)

        auth = FirebaseAuth.getInstance()

        join_email_et
        join_email_confirm_et.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val confirm = s.toString()
                val emailCheck = join_email_et.text.toString()
                if(emailCheck == confirm) {
                    join_email_check.text = "일치함"
                    emailFlag = true
                } else {
                    join_email_check.text = "일치하지 않음"
                    emailFlag = false
                }
            }
        })
        join_password_et
        join_password_confirm_et.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val confirm = s.toString()
                val passwordCheck = join_password_et.text.toString()
                if(passwordCheck == confirm) {
                    join_password_check.text = "일치함"
                    passwordFlag = true
                } else {
                    join_password_check.text = "일치하지 않음"
                    passwordFlag = false
                }
            }
        })
        join_button.setOnClickListener {
            if (emailFlag && passwordFlag) {
                val email = join_email_et.text.toString()
                val password = join_password_et.text.toString()
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val intent = Intent(this,SetNicknameActivity::class.java)
                        startActivity(intent)
                    }.addOnFailureListener {
                        Toast.makeText(this,"왜실패?${it.message},${it.toString()}",Toast.LENGTH_SHORT).show()
                        //이미 등록된 이메일 주소 확인해야함.
                    }
            }
        }
        join_cancel_button



    }
}