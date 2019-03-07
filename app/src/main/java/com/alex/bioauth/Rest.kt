package com.alex.bioauth

import com.alex.bioauth.model.*
import okhttp3.OkHttpClient.Builder
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory

import retrofit2.converter.jackson.JacksonConverterFactory
import rx.Observable
import java.util.concurrent.TimeUnit

class Rest {

    companion object {
        var service: Service? = null;

        fun init(endpoint: String) {
            val okHttpBuilder = Builder()
                .connectTimeout(45 * 1000, TimeUnit.MILLISECONDS)
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            val retrofitClient = Retrofit.Builder()
                .baseUrl(endpoint)
                .client(okHttpBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
            service = retrofitClient.create(Service::class.java)
        }

        fun createUser(user: User): Observable<Response<User>> {
            return service?.createUser(user)!!
        }

        fun getChallenge(userName: String): Observable<Response<Challenge>> {
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