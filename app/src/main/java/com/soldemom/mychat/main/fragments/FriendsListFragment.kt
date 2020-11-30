package com.soldemom.mychat.main.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel
import com.soldemom.mychat.main.recycler.FriendsListAdapter
import kotlinx.android.synthetic.main.fragment_friends_list.view.*

class FriendsListFragment : Fragment() {

    lateinit var viewModel: MainViewModel
    lateinit var user: User
    val db = FirebaseFirestore.getInstance()
    lateinit var friendsListAdapter: FriendsListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_friends_list, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        friendsListAdapter = FriendsListAdapter()
        view.friends_list_recycler_view.adapter = friendsListAdapter
        view.friends_list_recycler_view.layoutManager = LinearLayoutManager(requireContext())

        user = viewModel.user

        view.apply {
            friends_list_profile_name.text = user.name
            user.image?.let {
                Glide.with(this)
                    .load(user.image)
                    .centerCrop()
                    .into(friends_list_profile_img)
            }
            friends_list_introduce.text = user.introduce
        }

        db.collection("users").whereIn("uid",user.friendsList).get()
            .addOnSuccessListener {
                val friendsList = it.toObjects(User::class.java)
                viewModel.friendsList = friendsList
                friendsListAdapter.friendsList = friendsList
                friendsListAdapter.notifyDataSetChanged()
            }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onResume() {
        super.onResume()
    }
}