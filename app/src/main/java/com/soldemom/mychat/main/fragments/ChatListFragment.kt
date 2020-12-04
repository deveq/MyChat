package com.soldemom.mychat.main.fragments

import android.content.Intent
import android.graphics.Color
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
import java.util.*
import kotlin.Comparator


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

        db.collection("users").document(viewModel.user.uid).get()
            .addOnSuccessListener {
                viewModel.user = it.toObject(User::class.java)!!

                chatListAdapter = ChatListAdapter(viewModel.user, ::startChat)

                getChatList()
            }

        val toolbar = fragView.chat_list_toolbar
        toolbar.title = "채팅목록"
        toolbar.setBackgroundColor(Color.parseColor("#0D2A81"))
        toolbar.setTitleTextColor(Color.parseColor("#ffffff"))
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)


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
                        val tempChatroom = it.getValue(Chatroom::class.java)!!
                        chatrooms[it.key!!] = tempChatroom
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

    var treeComparator = Comparator<String> { chat1, chat2 ->
        chat1.compareTo(chat2) * -1
    }
}