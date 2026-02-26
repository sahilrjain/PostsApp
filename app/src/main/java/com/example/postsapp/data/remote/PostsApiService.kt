package com.example.postsapp.data.remote

import com.example.postsapp.data.model.PostDto
import retrofit2.http.GET

/**
 * Why Interface?
 * Dependency inversion Principle
 */
interface PostsApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}