package com.example.myapplication.services

import com.example.myapplication.datasource.models.UserModel
import retrofit2.Response
import retrofit2.http.*

interface ServicesInt {
    @POST("user")
    suspend fun createNewUser(@Body newUser: UserModel): Response<UserModel>

    @GET("user/{email}/{password}")
    suspend fun findUser(
        @Path("email") email: String,
        @Path("password") password: String
    ): Response<UserModel>

    @PUT("user/{id}")
    suspend fun updateUserDetails(
        @Path("id") id: String,
        @Body updateUser: UserModel
    ): Response<UserModel>

    @GET("contact/{number}")
    suspend fun getContactInfo(@Path("number") number: String): Response<UserModel>

    @GET("user/{number}")
    suspend fun getTokenField(@Path("number") number: String): Response<UserModel>
}