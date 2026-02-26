package com.example.postsapp.domain.repository

import com.example.postsapp.domain.model.Post

interface PostsRepository {
    suspend fun getPosts(): Result<List<Post>>
}