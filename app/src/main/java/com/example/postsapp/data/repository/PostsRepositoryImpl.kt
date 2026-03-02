package com.example.postsapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.postsapp.data.paging.PostsPagingSource
import com.example.postsapp.data.remote.PostsApiService
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostsRepository
import kotlinx.coroutines.flow.Flow
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

    /**
     * Why Pager?
     * Creates a reactive stream of PagingData using PostsPagingSource.
     * PagingConfig controls page size, prefetch distance, and placeholders.
     * pagingSourceFactory creates a new PagingSource for each refresh
     * (PagingSource instances are single-use).
     */
    override fun getPagedPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = PostsPagingSource.PAGE_SIZE,
                initialLoadSize = PostsPagingSource.PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { PostsPagingSource(apiService) }
        ).flow
    }
}
