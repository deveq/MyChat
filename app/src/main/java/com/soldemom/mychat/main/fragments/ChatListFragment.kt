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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.soldemom.mychat.ChatroomActivity
import com.soldemom.mychat.Model.Chatroom
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.R
import com.soldemom.mychat.main.MainViewModel
import com.soldemom.mychat.main.recycler.ChatListAdapter
import kotlinx.android.synthetic.main.activity_chatroom.view.*
import kotlinx.android.synthetic.main.fragment_chat_list.view.*


class ChatListFragment : Fragment() {
    lateinit var viewModel: MainViewModel
    val db = FirebaseFirestore.getInstance()
    val realtimeDB = FirebaseDatabase.getInstance()
    lateinit var chatListAdapter: ChatListAdapter
    lateinit var fragView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragView = inflater.inflate(R.layout.fragment_chat_list, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val toolbar = fragView.chat_list_toolbar
        toolbar.title = "채팅목록"
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        chatListAdapter = ChatListAdapter(viewModel.user, ::startChat)

        getChatList()


        //TODO 서버에 chatroom 객체가 하나도 없는 유저에 대한 처리



        return fragView
    }


    override fun onResume() {
        super.onResume()
        getChatList()
    }

    fun getChatList() {
        realtimeDB.getReference("chatrooms")
            .orderByChild("users/${viewModel.user.uid}")
            .equalTo(true)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatrooms = hashMapOf<String, Chatroom>()
                    snapshot.children.forEach {
                        chatrooms.put(it.key!!, it.getValue(Chatroom::class.java)!!)
                    }
                    if (fragView.chat_list_recycler_view.adapter == null) {
                        fragView.chat_list_recycler_view.adapter = chatListAdapter
                    }
                    chatListAdapter.chatrooms = chatrooms
                    chatListAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    fun startChat(myUser: User, destUser: User, chatroomId: String) {
        //ChatListAdapter에서 채팅item이 클릭되면 ChatroomActivity로 넘어갈 수 있도록 intent를 만듦
        val intent = Intent(requireActivity(), ChatroomActivity::class.java)

        //myUser, destUser, chatroomId를 넣어줘야함
        intent.putExtra("myUser",myUser)
        intent.putExtra("destinationUser",destUser)
        intent.putExtra("chatroomId",chatroomId)
        startActivity(intent)
    }
}