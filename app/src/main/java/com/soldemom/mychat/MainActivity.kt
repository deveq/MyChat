package com.soldemom.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.soldemom.mychat.Model.User
import com.soldemom.mychat.main.MainViewModel
import com.soldemom.mychat.main.fragments.ChatListFragment
import com.soldemom.mychat.main.fragments.FriendsListFragment
import com.soldemom.mychat.main.fragments.MainViewPagerAdapter
import com.soldemom.mychat.main.fragments.SettingFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.set_id_layout.view.*

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    private val TAG = MainActivity::class.java.simpleName
    lateinit var uid: String
    lateinit var userRef: DocumentReference
    lateinit var idRef: DocumentReference
    lateinit var user: User
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var user: User
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        auth = Firebase.auth
        uid = auth.currentUser!!.uid

        userRef = db.collection("users").document(uid)


        val fragmentNameList = listOf<String>("친구목록","채팅목록","설정")
        val fragmentList = listOf<Fragment>(
            FriendsListFragment(),
            ChatListFragment(),
            SettingFragment()
        )

        // MainActivity로 넘어온 후 db에서 받은 user객체에 id가 null이라면
        // id를 설정하는 창을 띄워줌
        userRef.get()
            .addOnSuccessListener {snapshot ->
                setId(snapshot)
                val mainViewPagerAdapter = MainViewPagerAdapter(this)
                mainViewPagerAdapter.fragmentList = fragmentList

                main_view_pager.adapter = mainViewPagerAdapter

                TabLayoutMediator(main_tab_layout, main_view_pager) { tab, position ->
                    tab.text = fragmentNameList[position]
                }.attach()
            }





    }

    private fun setId(snapshot: DocumentSnapshot) {
        user = snapshot.toObject(User::class.java)!!

        if (user.id == null) {
            val setIdView = LayoutInflater.from(this)
                .inflate(R.layout.set_id_layout,null,false)

            if (setIdView.parent != null) {
                (setIdView.parent as ViewGroup).removeView(setIdView)
            }

            val dialog = AlertDialog.Builder(this)
                .setView(setIdView)
                .create()

            setIdView.set_id_input.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }

                override fun afterTextChanged(s: Editable?) {
                    setIdView.set_id_confirm_button.isClickable = false
                }
            })


            setIdView.set_id_duplication_button.setOnClickListener {
                setIdView.apply {
                    val id = this.set_id_input.text.toString()
                    idRef = db.collection("id").document(id)
                    idRef.get()
                        .addOnSuccessListener {
                            val dbUser = it["uid"]
                            if (dbUser == null) {
                                Toast.makeText(this@MainActivity, "사용 가능한 아이디입니다.",Toast.LENGTH_SHORT).show()
                                set_id_confirm_button.isClickable = true
                            } else {
                                Toast.makeText(this@MainActivity, "이미 존재하는 아이디입니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            setIdView.set_id_confirm_button.setOnClickListener {
                setIdView.apply {
                    val id = this.set_id_input.text.toString()
                    db.runBatch { batch ->
                        batch.update(userRef,"id",id)
                        batch.set(idRef,hashMapOf("uid" to uid))
                    }.addOnSuccessListener {
                        viewModel.user = user.apply {
                            this.id = id
                        }
                        dialog.cancel()
                    }
                }
            }
            dialog.show()
        }
        viewModel.user = user
        Log.d("친구","${user.friendsList.size}명")
    }

}