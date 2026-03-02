package com.example.postsapp.data.remote

import com.example.postsapp.data.model.PostDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Why Interface?
 * Dependency inversion Principle
 */
interface PostsApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @GET("posts")
    suspend fun getPagedPosts(
        @Query("_page") page: Int,
        @Query("_limit") limit: Int
    ): List<PostDto>
}