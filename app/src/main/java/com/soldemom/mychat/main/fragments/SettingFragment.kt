package com.soldemom.mychat.main.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.LoginActivity
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment() {
    lateinit var viewModel: MainViewModel

    val auth = Firebase.auth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        view.setting_logout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            startActivity(intent)
        }


        return view
    }
}