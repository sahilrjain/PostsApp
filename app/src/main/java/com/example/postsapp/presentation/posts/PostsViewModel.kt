package com.example.postsapp.presentation.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.postsapp.domain.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    val repository: PostsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    init {
        handleEvent(PostsEvent.LoadPosts)
    }

    fun handleEvent(postEvent: PostsEvent) {
        when (postEvent) {
            is PostsEvent.RetryLoadPosts,
            is PostsEvent.LoadPosts -> loadPosts()
        }
    }

    private fun loadPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getPosts()
                .onSuccess { posts ->
                    _uiState.value = PostsUiState.Success(posts)
                }
                .onFailure { message ->
                    _uiState.value = PostsUiState.Error(
                        message.message ?: "Failed to load list."
                    )
                }
        }
    }
}
