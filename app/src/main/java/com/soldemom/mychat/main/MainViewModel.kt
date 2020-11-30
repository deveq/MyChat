package com.soldemom.mychat.main

import androidx.lifecycle.ViewModel
import com.soldemom.mychat.Model.User


class MainViewModel : ViewModel() {
    lateinit var user: User
    lateinit var friendsList: MutableList<User>

}