package com.soldemom.mychat.main.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.FindFriendActivity
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
    val searchFriendsList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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

        if (user.friendsList.size > 0) {
            db.collection("users").whereIn("uid",user.friendsList).get()
                .addOnSuccessListener {
                    val friendsList = it.toObjects(User::class.java)
                    viewModel.friendsList = friendsList
                    friendsListAdapter.friendsList = friendsList
                    friendsListAdapter.notifyDataSetChanged()
                }
        } else {
            viewModel.friendsList = mutableListOf<User>()
        }


        val toolbar =view.friends_list_toolbar
        toolbar.title = "친구목록"
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.friends_list_menu, menu)
        val searchItem = menu.findItem(R.id.friends_list_search_menu).actionView as SearchView

        searchItem.setOnCloseListener {
            friendsListAdapter.apply {
                friendsList = viewModel.friendsList
                notifyDataSetChanged()
            }
                false
            }
        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null && newText != "") {
                    searchUser(newText)
                } else {
                    friendsListAdapter.apply {
                        friendsList = viewModel.friendsList
                        notifyDataSetChanged()
                    }
                }
                return true
            }
        })
    }
    fun searchUser(name: String) {
        viewModel.friendsList.apply {
            searchFriendsList.clear()
            for (i in this) {

                if (i.name.indexOf(name)>=0) {
                    searchFriendsList.add(i)
                }
            }
            friendsListAdapter.friendsList = searchFriendsList
            friendsListAdapter.notifyDataSetChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.friends_list_add_menu) {
            val findFriendIntent = Intent(requireActivity(), FindFriendActivity::class.java)
            findFriendIntent.putExtra("id",user.id)
            findFriendIntent.putExtra("uid",user.uid)
            startActivity(findFriendIntent)
        }
        return super.onOptionsItemSelected(item)
    }
}