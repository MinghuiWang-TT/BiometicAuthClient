package com.alex.bioauth

import com.alex.bioauth.model.AuthenticationResponse
import com.alex.bioauth.model.Challenge
import com.alex.bioauth.model.ChallengeResponse
import com.alex.bioauth.model.User
import okhttp3.OkHttpClient.Builder
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

class Rest {


    companion object {
        var service: Service? = null;

        fun init(endpoint: String) {
            val okHttpBuilder = Builder()
                .connectTimeout(45, TimeUnit.MILLISECONDS)
            val retrofitClient = Retrofit.Builder()
                .baseUrl(endpoint)
                .client(okHttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
            service = retrofitClient.create(Service::class.java)
        }


        fun createUser(user: User): Observable<Response<User>> {
            return service?.createUser(user)!!
        }

        fun getChallennge(userName: String): Observable<Response<Challenge>> {
            return service?.getChallenge(userName)!!
        }

        fun responseChallenge(
            userName: String,
            response: ChallengeResponse
        ): Observable<Response<AuthenticationResponse>> {
            return service?.responseChallenge(userName, response)!!
        }
    }


}