package com.soldemom.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val toolbar = edit_profile_toolbar
        toolbar.title = "프로필 수정"
        setSupportActionBar(toolbar)

        val myUser = intent.getSerializableExtra("myUser") as User

        edit_profile_cancel_button
        edit_profile_confirm_button
        edit_profile_image
        edit_profile_introduce
        edit_profile_name

        edit_profile_name.setText(myUser.name)
        edit_profile_introduce.setText(myUser.introduce)
        myUser.image?.let {
            Glide.with(this)
                .load(it)
                .centerCrop()
                .into(edit_profile_image)
        }

        edit_profile_confirm_button.setOnClickListener {
            myUser.name = edit_profile_name.text.toString()
            myUser.introduce = edit_profile_introduce.text.toString()
            // TODO 이미지 수정 후 if문으로 flag가 true라면 넣어주기

            db.collection("users").document(myUser.uid)
                .set(myUser)
                .addOnSuccessListener {
                    Toast.makeText(this,"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                    intent.putExtra("myUser",myUser)
                    setResult(RESULT_OK, intent)
                    finish()
                }

        }

        edit_profile_cancel_button.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

}