package com.example.postsapp.domain.repository

import androidx.paging.PagingData
import com.example.postsapp.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {
    suspend fun getPosts(): Result<List<Post>>
    fun getPagedPosts(): Flow<PagingData<Post>>
}