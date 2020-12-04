package com.soldemom.mychat.fcm

import com.soldemom.mychat.Model.FcmReqModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

interface RetrofitService {
    @POST("/fcm")
    fun requestPush(
        @Body fcmReqModel: FcmReqModel
    ) : Call<String>



}