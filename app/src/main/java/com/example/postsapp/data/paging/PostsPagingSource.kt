package com.example.postsapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.postsapp.data.remote.PostsApiService
import com.example.postsapp.domain.model.Post

/**
 * Why PagingSource?
 * Defines how to load paginated data from the API.
 * Paging 3 calls load() automatically as the user scrolls,
 * requesting the next page when nearing the end of loaded data.
 *
 * Key types: PagingSource<Int, Post>
 *   - Int = page key type (1-based page numbers)
 *   - Post = the domain model items being loaded
 */
class PostsPagingSource(
    private val apiService: PostsApiService
) : PagingSource<Int, Post>() {

    companion object {
        const val STARTING_PAGE = 1
        const val PAGE_SIZE = 10
    }

    /**
     * Called by Paging 3 to load a page of data.
     * params.key is the page number (null on first load, so we default to STARTING_PAGE).
     * Returns LoadResult.Page on success with prev/next keys for navigation,
     * or LoadResult.Error on failure.
     */
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val response = apiService.getPagedPosts(
                page = page,
                limit = PAGE_SIZE
            )
            val posts = response.map { dto ->
                Post(
                    id = dto.id,
                    title = dto.title,
                    body = dto.body,
                    userId = dto.userId
                )
            }
            LoadResult.Page(
                data = posts,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (response.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    /**
     * Called on refresh (e.g., swipe-to-refresh).
     * Returns the page key to start loading from, based on the user's
     * current scroll position (anchorPosition).
     */
    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}
