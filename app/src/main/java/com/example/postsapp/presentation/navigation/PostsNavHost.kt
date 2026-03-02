package com.example.postsapp.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.postsapp.domain.model.Post
import com.example.postsapp.presentation.paginatedposts.PaginatedPostsScreen
import com.example.postsapp.presentation.postdetail.PostDetailScreen
import com.example.postsapp.presentation.posts.PostsScreen

data object HomeRoute
data object PostListRoute
data object PaginatedPostListRoute
data class PostDetailRoute(val post: Post)

@Composable
fun PostsNavHost() {
    val backStack = remember { mutableStateListOf<Any>(HomeRoute) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is HomeRoute -> NavEntry(key) {
                    HomeScreen(
                        onAllPostsClick = { backStack.add(PostListRoute) },
                        onPaginatedPostsClick = { backStack.add(PaginatedPostListRoute) }
                    )
                }

                is PostListRoute -> NavEntry(key) {
                    PostsScreen(
                        onPostClick = { post ->
                            backStack.add(PostDetailRoute(post))
                        }
                    )
                }

                is PaginatedPostListRoute -> NavEntry(key) {
                    PaginatedPostsScreen(
                        onPostClick = { post ->
                            backStack.add(PostDetailRoute(post))
                        },
                        onBack = { backStack.removeLastOrNull() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAllPostsClick: () -> Unit,
    onPaginatedPostsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { Text("Posts App") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Choose a loading strategy",
                style = MaterialTheme.typography.headlineSmall
            )
            Button(
                onClick = onAllPostsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("All Posts (Single Fetch)")
            }
            Button(
                onClick = onPaginatedPostsClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Paginated Posts (Paging 3)")
            }
        }
    }
}
