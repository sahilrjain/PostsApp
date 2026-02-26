package com.example.postsapp.data.repository

import com.example.postsapp.data.remote.PostsApiService
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostsRepository
import javax.inject.Inject

/**
 * Why:
 * Converts DTOs to domain models (separation of concerns)
 * Wraps result in Result type for elegant error handling
 * @Inject tells Hilt how to create this class
 */

class PostsRepositoryImpl @Inject constructor(
    private val apiService: PostsApiService
): PostsRepository {
    override suspend fun getPosts(): Result<List<Post>> {
        return try {
            val response = apiService.getPosts()
            // Convert data DTO to domain Post
            val posts = response.map {
                Post(
                    id = it.id,
                    title = it.title,
                    body = it.body,
                    userId = it.userId
                )
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
