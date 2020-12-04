package com.soldemom.mychat

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.soldemom.mychat.Model.User
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {

    val db = FirebaseFirestore.getInstance()
    var bitmap: Bitmap? = null
    lateinit var myUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val dialogPermissionListener =
            DialogOnDeniedPermissionListener.Builder
                .withContext(this)
                .withTitle("저장소 권한 요청")
                .withMessage("프로필 사진 업로드를 위해\n외부저장소 권한이 필요합니다.")
                .withButtonText("권한 설정하러 가기"){
                    val permissionItent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(permissionItent)
                }.build()

        Dexter
            .withContext(this)
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(dialogPermissionListener)
            .check()


        val toolbar = edit_profile_toolbar
        toolbar.title = "프로필 수정"
        setSupportActionBar(toolbar)

        myUser = intent.getSerializableExtra("myUser") as User

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

            // TODO 이미지 수정 후 bitmapt이 null이 아니라면
            if (bitmap != null) {
                val storage = Firebase.storage
                val baos = ByteArrayOutputStream()
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 10, baos)
                val imageData = baos.toByteArray()
                val imageRef = storage.reference.child("userImage/${myUser.uid}.jpg")

                var uploadTask = imageRef.putBytes(imageData)
                val urlTask = uploadTask.continueWithTask {task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageRef.downloadUrl
                }.addOnCompleteListener { task ->
                    val downloadUri = task.result
                    myUser.image = downloadUri.toString()
                    //비동기로 이루어지므로 bitmapt이 null이 아닐때도
                    //updateUser가 될 수 있도록
                    updateUser()
                }
            } else {
                updateUser()
            }
        }

        // TODO 이미지 클릭 시 이미지 수정으로
        edit_profile_image.setOnClickListener {
            val imageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(imageIntent, REQ_PROFILE_IMAGE)
        }

        edit_profile_cancel_button.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("취소하시겠습니까?")
                .setMessage("확인을 누르면 변경사항은 저장되지 않습니다.")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인") { _,_ ->
                    setResult(RESULT_CANCELED)
                    finish()
                }.create().show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 외부저장소에서 받아온 사진을 이미지에 넣어줌 (Storage에는 올리지 않음 아직)
        if (requestCode == REQ_PROFILE_IMAGE && resultCode == RESULT_OK) {
            val image = data!!.data!!
            bitmap = MediaStore.Images.Media.getBitmap(contentResolver, image)
            edit_profile_image.setImageBitmap(bitmap)
        }

    }

    fun updateUser() {
        db.collection("users").document(myUser.uid)
            .set(myUser)
            .addOnSuccessListener {
                Toast.makeText(this,"수정이 완료되었습니다.",Toast.LENGTH_SHORT).show()
                intent.putExtra("myUser",myUser)
                setResult(RESULT_OK, intent)
                finish()
            }
    }

    companion object {
        const val REQ_PROFILE_IMAGE = 1005
    }

}