package com.alex.bioauth

import com.alex.bioauth.model.AuthenticationResponse
import com.alex.bioauth.model.Challenge
import com.alex.bioauth.model.ChallengeResponse
import com.alex.bioauth.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import rx.Observable

interface Service {

    @POST("/user")
    fun createUser(@Body user: User): Observable<Response<User>>

    @POST("/challenge/{userName}")
    fun getChallenge(@Path("userName") userName: String): Observable<Response<Challenge>>
    
    @PUT("/challenge/{userName}")
    fun responseChallenge(@Path("userName") userName: String, @Body response: ChallengeResponse): Observable<Response<AuthenticationResponse>>
}