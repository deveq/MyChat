package com.soldemom.mychat.main.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel
import kotlinx.android.synthetic.main.fragment_chat_list.view.*


class ChatListFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_chat_list, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val toolbar = view.chat_list_toolbar
        toolbar.title = "채팅목록"

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.chat_list_menu, menu)
        val searchItem = menu.findItem(R.id.chat_list_search_menu).actionView as SearchView

        searchItem.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // TODO 검색 기능

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


    }

    fun setMenu(menu: Menu) {

    }

    override fun onResume() {
        super.onResume()
        db.collection("users").document(viewModel.user.uid).get()
            .addOnSuccessListener {

            }

    }
}