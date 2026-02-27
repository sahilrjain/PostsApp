package com.example.postsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.postsapp.domain.model.Post
import com.example.postsapp.presentation.postdetail.PostDetailScreen
import com.example.postsapp.presentation.posts.PostsScreen

data object PostListRoute
data class PostDetailRoute(val post: Post)

@Composable
fun PostsNavHost() {
    val backStack = remember { mutableStateListOf<Any>(PostListRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is PostListRoute -> NavEntry(key) {
                    PostsScreen(
                        onPostClick = { post ->
                            backStack.add(PostDetailRoute(post))
                        }
                    )
                }

                is PostDetailRoute -> NavEntry(key) {
                    PostDetailScreen(
                        post = key.post,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }

                else -> NavEntry(Unit) {}
            }
        }
    )
}
