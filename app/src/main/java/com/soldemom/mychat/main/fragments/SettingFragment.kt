package com.soldemom.mychat.main.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.LoginActivity
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {
    lateinit var viewModel: MainViewModel

    val auth = Firebase.auth
    val db = FirebaseFirestore.getInstance()
    lateinit var fragView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragView = inflater.inflate(R.layout.fragment_setting, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val toolbar = fragView.setting_toolbar
        toolbar.title = "설정"
        toolbar.setBackgroundColor(Color.parseColor("#0D2A81"))
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"))
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        fragView.setting_logout.setOnClickListener {
            db.collection("users").document(viewModel.user.uid)
                .update("fcmToken", FieldValue.delete())
                .addOnSuccessListener {
                    auth.signOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                }
        }

        val myUser = viewModel.user
        fragView.apply {
            setting_profile_name.text = myUser.name
            setting_introduce.text = myUser.introduce
            myUser.image?.let {
                Glide.with(fragView)
                    .load(it)
                    .centerCrop()
                    .into(setting_profile_img)
            }
        }

        fragView.setting_delete.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("탈퇴하시겠습니까?")
                .setNegativeButton("취소",null)
                .setPositiveButton("탈퇴") { _,_ ->
                    auth.currentUser!!.delete().addOnCompleteListener {
                        db.collection("users").document(myUser.uid).delete()
                    }
                }.create().show()
        }

        return fragView
    }
}