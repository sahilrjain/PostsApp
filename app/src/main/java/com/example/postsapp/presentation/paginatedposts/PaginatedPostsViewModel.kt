package com.example.postsapp.presentation.paginatedposts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.postsapp.domain.model.Post
import com.example.postsapp.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Why cachedIn(viewModelScope)?
 * Caches the PagingData so configuration changes (rotation)
 * don't re-trigger network calls. Without this, every
 * recomposition would restart paging from scratch.
 *
 * Why Flow<PagingData<Post>> instead of StateFlow?
 * PagingData has its own internal state management.
 * In the UI, collectAsLazyPagingItems() handles collection.
 */
@HiltViewModel
class PaginatedPostsViewModel @Inject constructor(
    repository: PostsRepository
) : ViewModel() {

    val postsFlow: Flow<PagingData<Post>> = repository.getPagedPosts()
        .cachedIn(viewModelScope)
}
