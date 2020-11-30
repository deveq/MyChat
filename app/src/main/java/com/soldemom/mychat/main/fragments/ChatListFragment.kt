package com.soldemom.mychat.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel


class ChatListFragment : Fragment() {
    lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        return view
    }
}