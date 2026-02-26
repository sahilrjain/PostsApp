package com.example.postsapp.domain.model

/**
 * Why is domain module separate from DTO?
 * This protects the app from API changes.
 */
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val userId: Int
)
