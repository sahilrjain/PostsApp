package com.example.postsapp.presentation.posts

import com.example.postsapp.domain.model.Post

sealed interface PostsUiState {
    data object Loading : PostsUiState
    data class Success(val posts: List<Post>) : PostsUiState
    data class Error(val errorMessage: String) : PostsUiState
}

sealed interface PostsEvent {
    data object LoadPosts : PostsEvent
    data object RetryLoadPosts : PostsEvent
}